package zk.fornax.http.framework.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import zk.fornax.common.exception.FornaxRuntimeException;
import zk.fornax.http.framework.annotation.Controller;
import zk.fornax.http.framework.utils.ClassScanUtils;

public class ControllerScanner {

    private ControllerScanner() {
    }

    public static List<ControllerMeta> initControllers(String controllerPackageName) {
        List<ControllerMeta> controllerMetas = new ArrayList<>();
        final Set<String> controllerClassNames = getControllerClassNames(controllerPackageName);
        for (String className : controllerClassNames) {
            try {
                final Class<?> controllerClass = Class.forName(className);
                final Controller annotation = controllerClass.getAnnotation(Controller.class);
                if (Objects.isNull(annotation)) {
                    continue;
                }
                controllerMetas.add(new ControllerMeta(controllerClass));
            } catch (ReflectiveOperationException exception) {
                throw new FornaxRuntimeException(exception);
            }
        }
        return controllerMetas;
    }

    private static Set<String> getControllerClassNames(String controllerPackageName) {
        Set<String> nameSet;
        try {
            nameSet = new HashSet<>(ClassScanUtils.getClassNames(controllerPackageName));
        } catch (IOException | URISyntaxException exception) {
            throw new FornaxRuntimeException(exception);
        }
        return nameSet;
    }

}
