package zk.fornax.gateway.filter;

import io.netty.handler.codec.http.HttpResponseStatus;
import reactor.core.publisher.Mono;

import zk.fornax.gateway.exception.ResponseStatusException;
import zk.fornax.http.core.ResponseHelper;
import zk.fornax.http.core.exchange.WebExchange;
import zk.fornax.http.core.filter.HttpApiFilter;
import zk.fornax.http.core.filter.HttpFilterChain;

public class ExceptionHandleHttpApiFilter implements HttpApiFilter {

    @Override
    public Mono<Void> filter(WebExchange webExchange, HttpFilterChain chain) {
        return chain.filter(webExchange).onErrorResume(throwable -> {
            if (throwable instanceof ResponseStatusException responseStatusException) {
                return ResponseHelper.sendJson(
                    webExchange.getResponse(),
                    responseStatusException.getHttpResponseStatus(),
                    responseStatusException.getMessage()
                );
            }
            return ResponseHelper.sendJson(webExchange.getResponse(), HttpResponseStatus.INTERNAL_SERVER_ERROR);
        });
    }

}
