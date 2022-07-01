package zk.fornax.http.core;

import java.util.Objects;
import java.util.Set;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;

import zk.fornax.common.httpapi.HttpApi;
import zk.fornax.common.httpapi.HttpMethod;
import zk.fornax.common.utils.ObjectUtils;

public class HttpApiMatcherImpl implements HttpApiMatcher {

    private static final AntPathMatcher ANT_PATH_MATCHER = AntPathMatcher.getDefaultInstance();

    public static final String VERSION_HEADER_NAME = "X-Grace-ApiVersion";

    @Override
    public Mono<Boolean> match(HttpServerRequest request, HttpApi httpApi) {
        return Mono.fromSupplier(() -> {
            boolean result = checkPath(request, httpApi);
            if (!result) {
                return false;
            }
            if (!isCorsPreFlightRequest(request)) {
                result = checkMethod(request, httpApi) && checkVersion(request, httpApi);
            }
            return result;
        });
    }

    private static boolean checkPath(HttpServerRequest request, HttpApi httpApi) {
        final String requestPath = request.fullPath();
        final String apiPath = httpApi.getPath();
        if (!ANT_PATH_MATCHER.isPattern(apiPath)) {
            return Objects.equals(apiPath, requestPath);
        }
        return ANT_PATH_MATCHER.match(apiPath, requestPath);
    }

    private static boolean checkMethod(HttpServerRequest request, HttpApi httpApi) {
        final Set<HttpMethod> allowedHttpMethods = httpApi.getHttpMethods();
        if (ObjectUtils.isEmpty(allowedHttpMethods)) {
            return false;
        }
        final io.netty.handler.codec.http.HttpMethod requestMethod = request.method();
        return allowedHttpMethods.contains(HttpMethod.resolve(requestMethod.name()));
    }

    private static boolean checkVersion(HttpServerRequest request, HttpApi httpApi) {
        String version = request.requestHeaders().get(VERSION_HEADER_NAME);
        final String apiVersion = httpApi.getVersion();
        if (ObjectUtils.isEmpty(version)) {
            return ObjectUtils.isEmpty(apiVersion);
        }
        return version.trim().equals(apiVersion);
    }

    private static boolean isCorsPreFlightRequest(HttpServerRequest request) {
        // 是否cors预检请求
        final HttpHeaders headers = request.requestHeaders();
        return request.method() == io.netty.handler.codec.http.HttpMethod.OPTIONS
            && headers.contains(HttpHeaderNames.ORIGIN)
            && headers.contains(HttpHeaderNames.ACCESS_CONTROL_REQUEST_METHOD);
    }

}
