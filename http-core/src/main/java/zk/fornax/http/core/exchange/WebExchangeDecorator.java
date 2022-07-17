package zk.fornax.http.core.exchange;

import java.security.Principal;
import java.util.Map;

import lombok.Getter;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import zk.fornax.common.httpapi.HttpApi;
import zk.fornax.http.core.session.WebSession;

public class WebExchangeDecorator implements WebExchange {

    @Getter
    private final WebExchange delegator;

    protected WebExchangeDecorator(WebExchange delegator) {
        this.delegator = delegator;
    }

    @Override
    public <T> T getAttribute(String name) {
        return delegator.getAttribute(name);
    }

    @Override
    public <T> T getAttributeOrDefault(String name, T defaultValue) {
        return delegator.getAttributeOrDefault(name, defaultValue);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return delegator.getAttributes();
    }

    @Override
    public HttpApi getHttpApi() {
        return delegator.getHttpApi();
    }

    @Override
    public Mono<Principal> getPrincipal() {
        return delegator.getPrincipal();
    }

    @Override
    public HttpServerRequest getRequest() {
        return delegator.getRequest();
    }

    @Override
    public <T> T getRequiredAttribute(String name) {
        return delegator.getRequiredAttribute(name);
    }

    @Override
    public HttpServerResponse getResponse() {
        return delegator.getResponse();
    }

    @Override
    public Mono<WebSession> getSession() {
        return delegator.getSession();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [delegator=" + getDelegator() + "]";
    }

}
