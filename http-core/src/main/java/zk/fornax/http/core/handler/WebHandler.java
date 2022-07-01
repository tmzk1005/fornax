package zk.fornax.http.core.handler;

import reactor.core.publisher.Mono;

import zk.fornax.http.core.exchange.WebExchange;

public interface WebHandler {

    Mono<Void> handle(WebExchange webExchange);

}
