package zk.fornax.gateway.filter.rewrite.body;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.NettyOutbound;
import reactor.netty.http.server.HttpServerResponse;

import zk.fornax.http.core.exchange.HttpServerResponseDecorator;
import zk.fornax.http.core.exchange.WebExchange;
import zk.fornax.http.core.filter.HttpApiFilter;
import zk.fornax.http.core.filter.HttpFilterChain;

public abstract class ModifyResponseBodyFilter implements HttpApiFilter {

    @Override
    public Mono<Void> filter(WebExchange webExchange, HttpFilterChain chain) {
        HttpServerResponse response = webExchange.getResponse();
        WebExchange mutatedWebExchange = webExchange.mutate().response(decorate(response)).build();
        return chain.filter(mutatedWebExchange);
    }

    private HttpServerResponse decorate(HttpServerResponse response) {
        return new HttpServerResponseDecorator(response) {

            @Override
            public NettyOutbound send(Publisher<? extends ByteBuf> dataStream) {
                Mono<ByteBuf> modifiedBody = ByteBufFlux.fromInbound(dataStream).aggregate().map(getBodyModifier()::convert);
                return getDelegator().send(modifiedBody);
            }

            @Override
            public HttpServerResponse headers(HttpHeaders headers) {
                headers.remove(HttpHeaderNames.CONTENT_LENGTH);
                headers.set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
                chunkedTransfer(true);
                getDelegator().headers(headers);
                return this;
            }

        };
    }

    protected abstract BodyModifier getBodyModifier();

}
