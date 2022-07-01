package zk.fornax.http.core.exchange;

import java.security.Principal;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import zk.fornax.common.httpapi.HttpApi;
import zk.fornax.http.core.session.AnonymousPrincipal;
import zk.fornax.http.core.session.WebSession;
import zk.fornax.http.core.session.WebSessionManager;

public class WebExchangeImpl implements WebExchange {

    @Getter
    private final HttpServerRequest request;

    @Getter
    private final HttpServerResponse response;

    @Getter
    private final HttpApi httpApi;

    @Getter
    private final Map<String, Object> attributes = new ConcurrentHashMap<>();

    @Getter
    private final Mono<WebSession> session;

    public WebExchangeImpl(
        HttpServerRequest request, HttpServerResponse response,
        HttpApi httpApi, WebSessionManager sessionManager
    ) {
        this.request = request;
        this.response = response;
        this.httpApi = httpApi;
        if (Objects.isNull(sessionManager)) {
            session = Mono.empty();
        } else {
            session = sessionManager.getSession(this).cache();
        }
    }

    @Override
    public Mono<Principal> getPrincipal() {
        if (Objects.isNull(session)) {
            return Mono.just(new AnonymousPrincipal());
        }
        return session.map(theSession -> {
            Principal principal = theSession.getAttribute(WebSession.ATTR_NAME_PRINCIPAL);
            if (Objects.isNull(principal)) {
                principal = new AnonymousPrincipal();
            }
            return principal;
        }).switchIfEmpty(Mono.just(new AnonymousPrincipal()));
    }

}
