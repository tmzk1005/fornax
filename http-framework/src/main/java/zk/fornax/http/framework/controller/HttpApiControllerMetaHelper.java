package zk.fornax.http.framework.controller;

import zk.fornax.common.httpapi.HttpApi;

public class HttpApiControllerMetaHelper {

    public static final String HTTP_API_CONTROLLER_META_EXTENSION_KEY = HttpApiControllerMetaHelper.class.getName() + "-ControllerMeta";

    private HttpApiControllerMetaHelper() {
    }

    public static void putControllerMeta(HttpApi httpApi, ControllerMeta controllerMeta) {
        httpApi.getExtensions().put(HTTP_API_CONTROLLER_META_EXTENSION_KEY, controllerMeta);
    }

    public static ControllerMeta getControllerMeta(HttpApi httpApi) {
        return httpApi.getExtensionInfo(HTTP_API_CONTROLLER_META_EXTENSION_KEY);
    }

}
