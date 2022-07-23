package zk.fornax.manager.security;

import java.util.Objects;

import reactor.core.publisher.Mono;

import zk.fornax.http.core.session.ReactiveRequestContextHolder;
import zk.fornax.http.core.session.WebSession;
import zk.fornax.http.framework.exception.AccessDeniedException;
import zk.fornax.manager.bean.Role;
import zk.fornax.manager.bean.po.User;

public class ContextHelper {

    private ContextHelper() {
    }

    public static Mono<WebSession> getSession() {
        return ReactiveRequestContextHolder.getContext()
            .flatMap(requestContext -> requestContext.getWebExchange().getSession());
    }

    public static Mono<User> getCurrentUser() {
        return getSession().map(session -> session.getAttribute(WebSession.ATTR_NAME_PRINCIPAL));
    }

    public static Mono<Boolean> isRole(Role... roles) {
        return getCurrentUser().map(user -> {
            Role role = user.getRole();
            if (Objects.isNull(role)) {
                return false;
            }
            for (Role r : roles) {
                if (role.equals(r)) {
                    return true;
                }
            }
            return false;
        }).switchIfEmpty(Mono.just(false));
    }

    public static Mono<Boolean> isRoleOrAccessDenied(Role... roles) {
        return isRole(roles).map(matched -> {
            if (Boolean.FALSE.equals(matched)) {
                throw new AccessDeniedException("没有访问权限!");
            }
            return matched;
        });
    }

}
