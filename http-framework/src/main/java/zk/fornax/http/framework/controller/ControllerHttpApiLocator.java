package zk.fornax.http.framework.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import reactor.core.publisher.Flux;

import zk.fornax.common.exception.FornaxRuntimeException;
import zk.fornax.common.httpapi.ApiStatus;
import zk.fornax.common.httpapi.HttpApi;
import zk.fornax.common.httpapi.HttpMethod;
import zk.fornax.http.core.HttpApiLocator;

public class ControllerHttpApiLocator implements HttpApiLocator {

    private static final Set<HttpMethod> ALL_HTTP_METHODS = Set.of(
        HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE,
        HttpMethod.HEAD, HttpMethod.TRACE, HttpMethod.OPTIONS, HttpMethod.PATCH
    );

    private final List<HttpApi> httpApis = new ArrayList<>();

    private final Flux<HttpApi> httpApiFlux;

    private final String controllerPackageName;

    public ControllerHttpApiLocator(String controllerPackageName) {
        this.controllerPackageName = controllerPackageName;
        parseControllerFromClassPath();
        httpApiFlux = Flux.fromIterable(httpApis);
    }

    @Override
    public Flux<HttpApi> getHttpApis(String path) {
        return httpApiFlux;
    }

    private void parseControllerFromClassPath() {
        final List<ControllerMeta> controllerMetas = ControllerScanner.initControllers(controllerPackageName);
        checkConflict(controllerMetas);
        for (ControllerMeta controllerMeta : controllerMetas) {
            httpApis.add(convertFromControllerMeta(controllerMeta));
        }
    }

    private static void checkConflict(List<ControllerMeta> controllerMetas) {
        Set<String> names = new HashSet<>();
        for (ControllerMeta controllerMeta : controllerMetas) {
            final String name = controllerMeta.getName();
            if (names.contains(name)) {
                throw new FornaxRuntimeException("ControllerName + " + name + " conflict.");
            }
            names.add(name);
        }
    }

    private static HttpApi convertFromControllerMeta(ControllerMeta controllerMeta) {
        final HttpApi httpApi = new HttpApi();
        final String name = controllerMeta.getName();
        httpApi.setId(name);
        httpApi.setApiStatus(ApiStatus.ONLINE);
        httpApi.setPath("/" + name + "/**");
        httpApi.setHttpMethods(ALL_HTTP_METHODS);
        httpApi.setLastModifiedTimestamp(System.currentTimeMillis());
        HttpApiControllerMetaHelper.putControllerMeta(httpApi, controllerMeta);
        return httpApi;
    }

}
