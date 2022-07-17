package zk.fornax.gateway.filter.rewrite.body;

import java.util.Objects;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.http.server.HttpServerRequest;

import zk.fornax.http.core.exchange.HttpServerRequestDecorator;
import zk.fornax.http.core.exchange.WebExchange;
import zk.fornax.http.core.filter.HttpApiFilter;
import zk.fornax.http.core.filter.HttpFilterChain;

public abstract class ModifyRequestBodyFilter implements HttpApiFilter {

    @Override
    public Mono<Void> filter(WebExchange webExchange, HttpFilterChain chain) {
        HttpServerRequest request = webExchange.getRequest();
        WebExchange mutatedWebExchange = webExchange.mutate().request(decorate(request)).build();
        return chain.filter(mutatedWebExchange);
    }

    private HttpServerRequest decorate(HttpServerRequest request) {
        return new HttpServerRequestDecorator(request) {

            @Override
            public HttpHeaders requestHeaders() {
                HttpHeaders httpHeaders = getDecorator().requestHeaders();
                String newContentType = getBodyModifier().getNewContentType();
                if (Objects.nonNull(newContentType)) {
                    httpHeaders.set(HttpHeaderNames.CONTENT_TYPE, newContentType);
                }
                // 不知道转换后的长度，因此删除Content-Length头
                httpHeaders.remove(HttpHeaderNames.CONTENT_LENGTH);
                httpHeaders.set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
                return httpHeaders;
            }

            @Override
            public ByteBufFlux receive() {
                return ByteBufFlux.fromInbound(getDecorator().receive().aggregate().map(getBodyModifier()::convert));
            }

        };
    }

    protected abstract BodyModifier getBodyModifier();

}
