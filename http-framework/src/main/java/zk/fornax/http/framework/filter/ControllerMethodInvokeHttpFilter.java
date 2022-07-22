package zk.fornax.http.framework.filter;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;

import zk.fornax.common.exception.FornaxRuntimeException;
import zk.fornax.common.httpapi.HttpApi;
import zk.fornax.common.httpapi.HttpMethod;
import zk.fornax.common.rest.RestShuck;
import zk.fornax.common.utils.JsonUtil;
import zk.fornax.http.core.AntPathMatcher;
import zk.fornax.http.core.ResponseHelper;
import zk.fornax.http.core.exchange.WebExchange;
import zk.fornax.http.core.filter.HttpApiFilter;
import zk.fornax.http.core.filter.HttpFilterChain;
import zk.fornax.http.framework.annotation.RequestBody;
import zk.fornax.http.framework.annotation.RequestParam;
import zk.fornax.http.framework.annotation.ResponseStatus;
import zk.fornax.http.framework.annotation.Route;
import zk.fornax.http.framework.controller.ControllerMeta;
import zk.fornax.http.framework.controller.HttpApiControllerMetaHelper;
import zk.fornax.http.framework.exception.BadRequestException;
import zk.fornax.http.framework.exception.InternalServerError;
import zk.fornax.http.framework.exception.RequestRelatedException;
import zk.fornax.http.framework.utils.HttpPathNormalizer;
import zk.fornax.http.framework.validate.ParameterValidator;
import zk.fornax.http.framework.validate.PathVariable;

@Log4j2
public class ControllerMethodInvokeHttpFilter implements HttpApiFilter {

    @Override
    public Mono<Void> filter(WebExchange webExchange, HttpFilterChain chain) {
        HttpApi httpApi = webExchange.getHttpApi();
        ControllerMeta controllerMeta = HttpApiControllerMetaHelper.getControllerMeta(httpApi);
        if (Objects.isNull(controllerMeta)) {
            return sendNotFound(webExchange);
        }
        HttpMethod requestMethod = HttpMethod.resolve(webExchange.getRequest().method().name());
        String requestPath = webExchange.getRequest().fullPath();
        String requestPathWithOutPrefix = HttpPathNormalizer.removeFirstSegment(requestPath);
        final Method method = controllerMeta.getMethod(requestMethod, requestPathWithOutPrefix);
        if (Objects.isNull(method)) {
            return sendNotFound(webExchange);
        }
        return new InvokerContext(controllerMeta.getController(), method, webExchange, requestPathWithOutPrefix).invoke()
            .onErrorResume(throwable -> handleException(throwable, webExchange, controllerMeta, method))
            .then(chain.filter(webExchange));
    }

    private Mono<Void> sendNotFound(WebExchange webExchange) {
        log.info("Not found: {} {}", webExchange.getRequest().method(), webExchange.getRequest().fullPath());
        return ResponseHelper.sendJson(webExchange.getResponse(), HttpResponseStatus.NOT_FOUND);
    }

    private Mono<Void> handleException(Throwable throwable, WebExchange webExchange, ControllerMeta controllerMeta, Method method) {
        if (throwable instanceof RequestRelatedException rre) {
            return handleRequestRelatedException(rre, webExchange);
        } else {
            log.error("Failed to invoke controller method {}.{}", controllerMeta.getController().getClass().getName(), method.getName(), throwable);
            return fallbackHandleException(throwable, webExchange);
        }
    }

    private Mono<Void> handleRequestRelatedException(RequestRelatedException exception, WebExchange webExchange) {
        String message = exception.getMessage();
        log.info("{} : {} : {}", exception.getClass().getSimpleName(), RequestRelatedException.requestInfo(webExchange.getRequest()), message);
        ResponseStatus annotation = exception.getClass().getAnnotation(ResponseStatus.class);
        HttpResponseStatus httpResponseStatus = Objects.nonNull(annotation) ? HttpResponseStatus.valueOf(annotation.code()) : HttpResponseStatus.OK;
        return ResponseHelper.sendJson(webExchange.getResponse(), httpResponseStatus, exception.getCode(), message);
    }

    private Mono<Void> fallbackHandleException(Throwable throwable, WebExchange webExchange) {
        HttpResponseStatus httpResponseStatus;
        String message;
        ResponseStatus annotation = throwable.getClass().getAnnotation(ResponseStatus.class);
        if (Objects.nonNull(annotation)) {
            httpResponseStatus = HttpResponseStatus.valueOf(annotation.code());
            message = annotation.message().equals("") ? httpResponseStatus.reasonPhrase() : annotation.message();
        } else {
            httpResponseStatus = HttpResponseStatus.INTERNAL_SERVER_ERROR;
            message = httpResponseStatus.reasonPhrase();
        }
        return ResponseHelper.sendJson(webExchange.getResponse(), httpResponseStatus, message);
    }

    private static class InvokerContext {

        private final WebExchange webExchange;

        private final Object controller;

        private final Method method;

        private final String pathWithoutPrefix;

        private Object[] args;

        /**
         * 是否存在被 @RequestBody 注解修饰的参数, -1表示没有，>=0 表示该参数的索引位置，最多只能有一个.
         */
        private final AtomicReference<Integer> requestBodyParamIndexRef = new AtomicReference<>(-1);

        public InvokerContext(Object controller, Method method, WebExchange webExchange, String pathWithoutPrefix) {
            this.webExchange = webExchange;
            this.controller = controller;
            this.method = method;
            this.pathWithoutPrefix = pathWithoutPrefix;
        }

        public Mono<Void> invoke() {
            return parseOutArgs().then(doInvoke()).flatMap(this::handleInvokeResult);
        }

        private Mono<Void> parseOutArgs() {
            Parameter[] parameters = method.getParameters();
            args = new Object[parameters.length];
            if (args.length == 0) {
                return Mono.empty();
            }
            Route routeAnn = method.getAnnotation(Route.class);
            Objects.requireNonNull(routeAnn);
            String routePath = HttpPathNormalizer.normalize(routeAnn.path());
            Map<String, String> pathVariables = AntPathMatcher.getDefaultInstance().extractUriTemplateVariables(routePath, pathWithoutPrefix);
            Map<String, List<String>> requestParameters = new QueryStringDecoder(webExchange.getRequest().uri()).parameters();
            for (int i = 0; i < parameters.length; ++i) {
                PathVariable pathVariableAnn;
                RequestParam requestParamAnn;
                if ((pathVariableAnn = parameters[i].getAnnotation(PathVariable.class)) != null) {
                    String value = pathVariables.get(pathVariableAnn.value());
                    checkRequiredParameter(value, true, "Path variable " + pathVariableAnn.value() + " required.");
                    args[i] = transformPathVariableValue(parameters[i], value);
                } else if ((requestParamAnn = parameters[i].getAnnotation(RequestParam.class)) != null) {
                    args[i] = parseRequestParamValue(parameters[i], requestParamAnn, requestParameters);
                } else if (parameters[i].getAnnotation(RequestBody.class) != null) {
                    if (requestBodyParamIndexRef.get() >= 0) {
                        // 进入到此,说明至少优2个被@RequestBody修饰的参数,不可以
                        log.error("More then one @RequestBody used in controller method {}.{}", controller.getClass().getName(), method.getName());
                        throw new InternalServerError();
                    }
                    requestBodyParamIndexRef.set(i);
                    args[i] = deserializeRequestStreamToObject(parameters[i], webExchange.getRequest().receive());
                    checkRequiredParameter(args[i], true, "Request body required.");
                }
            }
            return Mono.empty();
        }

        private Mono<?> doInvoke() {
            int requestBodyParamIndex = requestBodyParamIndexRef.get();
            if (requestBodyParamIndex >= 0) {
                return ((Mono<?>) args[requestBodyParamIndex]).map(bodyParamValue -> {
                    args[requestBodyParamIndex] = bodyParamValue;
                    return validatorThenInvoke(method, controller, args);
                });
            } else {
                return Mono.justOrEmpty(validatorThenInvoke(method, controller, args));
            }
        }

        private void checkRequiredParameter(Object value, boolean required, String message) {
            if (required && Objects.isNull(value)) {
                throw new BadRequestException(message);
            }
        }

        private Object transformPathVariableValue(Parameter parameter, String rawValue) throws BadRequestException {
            try {
                return transform(parameter.getType(), rawValue);
            } catch (NumberFormatException numberFormatException) {
                throw new BadRequestException("Require parameter " + parameter.getName());
            }
        }

        private Object transform(Class<?> type, String value) {
            if (Objects.isNull(value)) {
                return null;
            }
            String typeName = type.getName();
            return switch (typeName) {
                case "int", "java.lang.Integer" -> Integer.parseInt(value);
                case "long", "java.lang.Long" -> Long.parseLong(value);
                case "double", "java.lang.Double" -> Double.parseDouble(value);
                // support all number types ? just int long double for now
                default -> value;
            };
        }

        private Object parseRequestParamValue(Parameter parameter, RequestParam requestParamAnn, Map<String, List<String>> requestParameters)
            throws BadRequestException {
            String parameterName = requestParamAnn.name();
            if (parameterName.equals("")) {
                throw new FornaxRuntimeException("RequestParam annotation has no name configured! " + parameter.toString());
            }
            List<String> value = requestParameters.get(parameterName);
            checkRequiredParameter(value, requestParamAnn.required(), "Parameter " + parameterName + " required.");
            if (!requestParamAnn.required() && Objects.isNull(value)) {
                String defaultValue = requestParamAnn.defaultValue();
                if (defaultValue.equals(RequestParam.NULL)) {
                    value = new ArrayList<>();
                    value.add(null);
                } else {
                    value = List.of(defaultValue);
                }
            }
            return transformRequestParameterValue(parameter, value);
        }

        private Object transformRequestParameterValue(Parameter parameter, List<String> rawValue) throws BadRequestException {
            Class<?> type = parameter.getType();
            try {
                if (type.isArray()) {
                    Class<?> itemType = type.componentType();
                    Object array = Array.newInstance(itemType, rawValue.size());
                    for (int i = 0; i < rawValue.size(); ++i) {
                        Array.set(array, i, transform(itemType, rawValue.get(i)));
                    }
                    return array;
                }
                return transform(type, rawValue.get(0));
            } catch (NumberFormatException numberFormatException) {
                throw new BadRequestException("Require parameter " + parameter.getName());
            }
        }

        private Object deserializeRequestStreamToObject(Parameter parameter, ByteBufFlux byteBufFlux) {
            return byteBufFlux.aggregate().asString(StandardCharsets.UTF_8).map(jsonContent -> {
                final Class<?> type = parameter.getType();
                if (type.isAssignableFrom(String.class)) {
                    return jsonContent;
                }
                try {
                    return JsonUtil.readValue(jsonContent, parameter.getType());
                } catch (JsonProcessingException jsonProcessingException) {
                    throw new BadRequestException("Request body deserialize failed.", jsonProcessingException);
                }
            }).switchIfEmpty(Mono.error(new BadRequestException("Request body required!")));
        }

        private Object validatorThenInvoke(Method method, Object controller, Object... parameterValues) {
            try {
                final ParameterValidator parameterValidator = new ParameterValidator();
                parameterValidator.validate(method.getParameters(), parameterValues);
                if (parameterValidator.hasError()) {
                    throw new BadRequestException(parameterValidator.getErrorMessage());
                }
                return method.invoke(controller, parameterValues);
            } catch (ReflectiveOperationException exception) {
                throw new InternalServerError(exception);
            }
        }

        private Mono<Void> handleInvokeResult(Object invokeResult) {
            Object finalInvokeResult = invokeResult;
            if (finalInvokeResult instanceof Flux<?> flux) {
                finalInvokeResult = flux.collectList();
            }
            if (finalInvokeResult instanceof Mono<?> mono) {
                // 需要把Mono里的东西序列化为json
                return mono.flatMap(data -> {
                    String jsonContent;
                    try {
                        jsonContent = JsonUtil.toJson(RestShuck.success(data));
                    } catch (JsonProcessingException exception) {
                        throw new InternalServerError(exception);
                    }
                    return ResponseHelper.sendJson(webExchange.getResponse(), jsonContent);
                }).switchIfEmpty(ResponseHelper.sendJson(webExchange.getResponse(), HttpResponseStatus.OK));
            } else if (finalInvokeResult instanceof CharSequence charSequence) {
                return ResponseHelper.send(webExchange.getResponse(), HttpResponseStatus.OK, HttpHeaderValues.TEXT_PLAIN.toString(), charSequence.toString());
            } else {
                log.error(
                    "Controller method {}.{} returned unsupported type {}",
                    controller.getClass().getName(), method.getName(), finalInvokeResult.getClass().getName()
                );
                return ResponseHelper.sendJson(webExchange.getResponse(), HttpResponseStatus.INTERNAL_SERVER_ERROR);
            }
        }

    }

}
