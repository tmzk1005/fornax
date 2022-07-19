package zk.fornax.manager.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import zk.fornax.common.utils.ObjectUtils;
import zk.fornax.manager.bean.Role;
import zk.fornax.manager.security.HasRole;
import zk.fornax.manager.security.RoleChecker;
import zk.fornax.manager.service.impl.UserServiceImpl;

@Log4j2
public class ServiceFactory {

    private static final Map<Class<?>, Object> SERVICE_MAP = new HashMap<>();

    static {
        SERVICE_MAP.put(UserService.class, new SecurityProxy<>(UserService.class, new UserServiceImpl()).getProxy());
    }

    private ServiceFactory() {
    }

    @SuppressWarnings("unchecked")
    public static <S> S get(Class<S> serviceClass) {
        return (S) SERVICE_MAP.get(serviceClass);
    }

    private static class SecurityProxy<S> implements InvocationHandler {

        private final Class<S> serviceInterface;

        private final S serviceNormalInstance;

        private final Map<Method, Role[]> rolesCache = new ConcurrentHashMap<>();

        public SecurityProxy(Class<S> serviceInterface, S serviceNormalInstance) {
            this.serviceInterface = serviceInterface;
            this.serviceNormalInstance = serviceNormalInstance;
        }

        @SuppressWarnings("unchecked")
        public S getProxy() {
            return (S) Proxy.newProxyInstance(serviceNormalInstance.getClass().getClassLoader(), new Class[] { serviceInterface }, this);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (!rolesCache.containsKey(method)) {
                rolesCache.put(method, parseNeedRoles(method));
            }
            final Role[] needRoles = rolesCache.get(method);
            if (ObjectUtils.isEmpty(needRoles)) {
                return method.invoke(serviceNormalInstance, args);
            }
            final Mono<Boolean> passed = RoleChecker.isRoleOrAccessDenied(needRoles);
            final Class<?> returnType = method.getReturnType();
            if (Mono.class.isAssignableFrom(returnType)) {
                return passed.then((Mono<?>) method.invoke(serviceNormalInstance, args));
            } else if (Flux.class.isAssignableFrom(returnType)) {
                return passed.thenMany((Flux<?>) method.invoke(serviceNormalInstance, args));
            } else {
                log.warn(
                    "Method {} of interface {} does not has a Mono of Flux return type, so no roles check will be apply!",
                    method.getName(), serviceInterface.getName()
                );
                return method.invoke(serviceNormalInstance, args);
            }
        }

        private static Role[] parseNeedRoles(Method method) {
            final HasRole annotation = method.getAnnotation(HasRole.class);
            if (Objects.isNull(annotation)) {
                return new Role[0];
            }
            final Role[] value = annotation.value();
            if (ObjectUtils.isEmpty(value)) {
                return new Role[0];
            }
            return value;
        }

    }

}
