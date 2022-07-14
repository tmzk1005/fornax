package zk.fornax.http.framework.controller;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;

import zk.fornax.common.exception.FornaxRuntimeException;
import zk.fornax.common.httpapi.HttpMethod;
import zk.fornax.http.core.AntPathMatcher;
import zk.fornax.http.framework.annotation.Route;
import zk.fornax.http.framework.utils.HttpPathNormalizer;

public class ControllerMeta {

    private static final AntPathMatcher ANT_PATH_MATCHER = AntPathMatcher.getDefaultInstance();

    private final Class<?> controllerClass;

    @Getter
    private final String name;

    @Getter
    private final Object controller;

    @Getter
    private final Map<HttpMethod, Map<String, Method>> methods = new ConcurrentHashMap<>();

    public ControllerMeta(Object controller) {
        this.controller = controller;
        this.controllerClass = controller.getClass();
        this.name = parseName();
        parseRouteMethods();
    }

    public Method getMethod(HttpMethod httpMethod, String requestPath) {
        final Map<String, Method> map = methods.get(httpMethod);
        if (Objects.isNull(map)) {
            return null;
        }
        for (Map.Entry<String, Method> entry : map.entrySet()) {
            final String pathPattern = entry.getKey();
            if (!ANT_PATH_MATCHER.isPattern(pathPattern)) {
                if (pathPattern.equals(requestPath)) {
                    return entry.getValue();
                } else {
                    continue;
                }
            }
            if (ANT_PATH_MATCHER.match(pathPattern, requestPath)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private void parseRouteMethods() {
        final Method[] allPublicMethods = controllerClass.getMethods();
        for (Method method : allPublicMethods) {
            final Route routeAnn = method.getAnnotation(Route.class);
            if (Objects.isNull(routeAnn)) {
                continue;
            }
            final HttpMethod httpMethod = routeAnn.method();
            methods.putIfAbsent(httpMethod, new ConcurrentHashMap<>());
            String path = routeAnn.path();
            path = HttpPathNormalizer.normalize(path);
            final Map<String, Method> path2Methods = methods.get(httpMethod);
            if (path2Methods.containsKey(path)) {
                throw new FornaxRuntimeException("Method route path " + path + "conflict.");
            }
            path2Methods.put(path, method);
        }
    }

    private String parseName() {
        String simpleName = controllerClass.getSimpleName();
        String suffix = "Controller";
        if (simpleName.endsWith(suffix)) {
            simpleName = simpleName.substring(0, simpleName.length() - suffix.length());
        }
        return simpleName;
    }

}
