package zk.fornax.http.core.filter;

import reactor.core.publisher.Mono;

import zk.fornax.http.core.exchange.WebExchange;

public interface HttpApiFilter {

    Mono<Void> filter(WebExchange webExchange, HttpFilterChain chain);

}