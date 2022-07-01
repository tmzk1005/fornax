package zk.fornax.http.core.session;

import reactor.core.publisher.Mono;

import zk.fornax.http.core.exchange.WebExchange;

public interface WebSessionManager {

    Mono<WebSession> getSession(WebExchange exchange);

}
