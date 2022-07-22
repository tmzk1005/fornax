package zk.fornax.http.framework.filter;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

import zk.fornax.http.core.ResponseHelper;
import zk.fornax.http.core.exchange.WebExchange;
import zk.fornax.http.core.filter.HttpApiFilter;
import zk.fornax.http.core.filter.HttpFilterChain;

@Log4j2
public class ExceptionHandleHttpApiFilter implements HttpApiFilter {

    public static final String MSG = "服务器内部错误";

    @Override
    public Mono<Void> filter(WebExchange webExchange, HttpFilterChain chain) {
        return chain.filter(webExchange).onErrorResume(throwable -> {
            // 一般不会走到这里来,just in case
            log.error("A exception not handled by other filters: ", throwable);
            return ResponseHelper.sendJson(webExchange.getResponse(), HttpResponseStatus.INTERNAL_SERVER_ERROR, MSG);
        });
    }

}
