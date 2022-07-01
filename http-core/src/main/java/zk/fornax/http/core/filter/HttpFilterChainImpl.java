package zk.fornax.http.core.filter;

import java.util.List;

import reactor.core.publisher.Mono;

import zk.fornax.http.core.exchange.WebExchange;

public class HttpFilterChainImpl implements HttpFilterChain {

    private int index;

    private final List<HttpApiFilter> filters;

    public HttpFilterChainImpl(List<HttpApiFilter> filters) {
        this.filters = filters;
        this.index = 0;
    }

    @Override
    public Mono<Void> filter(WebExchange webExchange) {
        return Mono.defer(() -> {
            if (this.index < filters.size()) {
                HttpApiFilter filter = filters.get(this.index);
                this.index++;
                return filter.filter(webExchange, this);
            } else {
                return Mono.empty();
            }
        });
    }

}