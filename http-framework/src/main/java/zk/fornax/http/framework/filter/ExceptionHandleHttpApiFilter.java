package zk.fornax.http.framework.filter;

import reactor.core.publisher.Mono;

import zk.fornax.http.core.exchange.WebExchange;
import zk.fornax.http.core.filter.HttpApiFilter;
import zk.fornax.http.core.filter.HttpFilterChain;

public class ExceptionHandleHttpApiFilter implements HttpApiFilter {

    @Override
    public Mono<Void> filter(WebExchange webExchange, HttpFilterChain chain) {
        return chain.filter(webExchange).onErrorResume(throwable -> {
            // TODO
            throwable.printStackTrace();
            return Mono.empty();
        });
    }

}
