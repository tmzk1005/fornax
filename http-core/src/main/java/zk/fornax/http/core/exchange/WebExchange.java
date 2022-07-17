package zk.fornax.http.core.exchange;

import java.security.Principal;
import java.util.Map;
import java.util.Objects;

import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import zk.fornax.common.httpapi.HttpApi;
import zk.fornax.http.core.session.WebSession;

public interface WebExchange {

    HttpServerRequest getRequest();

    HttpServerResponse getResponse();

    HttpApi getHttpApi();

    Map<String, Object> getAttributes();

    @SuppressWarnings("unchecked")
    default <T> T getAttribute(String name) {
        return (T) getAttributes().get(name);
    }

    default <T> T getRequiredAttribute(String name) {
        T value = getAttribute(name);
        Objects.requireNonNull(value, "Required attribute '" + name + "' is missing");
        return value;
    }

    @SuppressWarnings("unchecked")
    default <T> T getAttributeOrDefault(String name, T defaultValue) {
        return (T) getAttributes().getOrDefault(name, defaultValue);
    }

    Mono<WebSession> getSession();

    Mono<Principal> getPrincipal();

    default Builder mutate() {
        return new DefaultWebExchangeBuilder(this);
    }

    interface Builder {

        Builder request(HttpServerRequest request);

        Builder response(HttpServerResponse response);

        WebExchange build();

    }

}
