package zk.fornax.http.core.handler;

import java.util.List;

import reactor.core.publisher.Mono;

import zk.fornax.http.core.exchange.WebExchange;
import zk.fornax.http.core.filter.HttpApiFilter;
import zk.fornax.http.core.filter.HttpFilterChainImpl;

public record ChainBasedWebHandler(List<HttpApiFilter> filters) implements WebHandler {

    @Override
    public Mono<Void> handle(WebExchange webExchange) {
        return new HttpFilterChainImpl(filters).filter(webExchange);
    }

}