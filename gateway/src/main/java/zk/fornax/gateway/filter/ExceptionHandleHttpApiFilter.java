package zk.fornax.gateway.filter;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

import zk.fornax.gateway.exception.ResponseStatusException;
import zk.fornax.http.core.ResponseHelper;
import zk.fornax.http.core.exchange.WebExchange;
import zk.fornax.http.core.filter.HttpApiFilter;
import zk.fornax.http.core.filter.HttpFilterChain;

@Log4j2
public class ExceptionHandleHttpApiFilter implements HttpApiFilter {

    @Override
    public Mono<Void> filter(WebExchange webExchange, HttpFilterChain chain) {
        return chain.filter(webExchange).onErrorResume(throwable -> {
            log.error("An exception did not handled by filters before ExceptionHandleHttpApiFilter.", throwable);
            HttpResponseStatus httpResponseStatus;
            String message;
            if (throwable instanceof ResponseStatusException responseStatusException) {
                httpResponseStatus = responseStatusException.getHttpResponseStatus();
                message = responseStatusException.getMessage();
            } else {
                httpResponseStatus = HttpResponseStatus.INTERNAL_SERVER_ERROR;
                message = httpResponseStatus.reasonPhrase();
            }
            return ResponseHelper.sendJson(webExchange.getResponse(), httpResponseStatus, message);
        });
    }

}
