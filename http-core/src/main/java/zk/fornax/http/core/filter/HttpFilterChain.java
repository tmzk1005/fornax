package zk.fornax.http.core.filter;

import reactor.core.publisher.Mono;

import zk.fornax.http.core.exchange.WebExchange;

public interface HttpFilterChain {

    Mono<Void> filter(WebExchange webExchange);

}