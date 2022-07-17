package zk.fornax.http.core.exchange;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.cookie.Cookie;
import lombok.Getter;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.NettyOutbound;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.WebsocketServerSpec;
import reactor.netty.http.websocket.WebsocketInbound;
import reactor.netty.http.websocket.WebsocketOutbound;

public class HttpServerResponseDecorator implements HttpServerResponse {

    @Getter
    private final HttpServerResponse delegator;

    public HttpServerResponseDecorator(HttpServerResponse delegator) {
        this.delegator = delegator;
    }

    @Override
    public HttpServerResponse addCookie(Cookie cookie) {
        delegator.addCookie(cookie);
        return this;
    }

    @Override
    public HttpServerResponse addHeader(CharSequence name, CharSequence value) {
        delegator.addHeader(name, value);
        return this;
    }

    @Override
    public HttpServerResponse chunkedTransfer(boolean chunked) {
        delegator.chunkedTransfer(chunked);
        return this;
    }

    @Override
    public HttpServerResponse compression(boolean compress) {
        delegator.compression(compress);
        return this;
    }

    @Override
    public boolean hasSentHeaders() {
        return delegator.hasSentHeaders();
    }

    @Override
    public HttpServerResponse header(CharSequence name, CharSequence value) {
        delegator.header(name, value);
        return this;
    }

    @Override
    public HttpServerResponse headers(HttpHeaders headers) {
        delegator.headers(headers);
        return this;
    }

    @Override
    public HttpServerResponse keepAlive(boolean keepAlive) {
        this.delegator.keepAlive(keepAlive);
        return this;
    }

    @Override
    public HttpHeaders responseHeaders() {
        return delegator.responseHeaders();
    }

    @Override
    public Mono<Void> send() {
        return delegator.send();
    }

    @Override
    public NettyOutbound sendHeaders() {
        return delegator.sendHeaders();
    }

    @Override
    public Mono<Void> sendNotFound() {
        return delegator.sendNotFound();
    }

    @Override
    public Mono<Void> sendRedirect(String location) {
        return delegator.sendRedirect(location);
    }

    @Override
    public Mono<Void> sendWebsocket(
        BiFunction<? super WebsocketInbound, ? super WebsocketOutbound, ? extends Publisher<Void>> websocketHandler,
        WebsocketServerSpec websocketServerSpec
    ) {
        return delegator.sendWebsocket(websocketHandler, websocketServerSpec);
    }

    @Override
    public HttpServerResponse sse() {
        delegator.sse();
        return this;
    }

    @Override
    public HttpResponseStatus status() {
        return delegator.status();
    }

    @Override
    public HttpServerResponse status(HttpResponseStatus status) {
        delegator.status(status);
        return this;
    }

    @Override
    public HttpServerResponse trailerHeaders(Consumer<? super HttpHeaders> trailerHeaders) {
        delegator.trailerHeaders(trailerHeaders);
        return this;
    }

    @Override
    public HttpServerResponse withConnection(Consumer<? super Connection> withConnection) {
        delegator.withConnection(withConnection);
        return this;
    }

    @Override
    public ByteBufAllocator alloc() {
        return delegator.alloc();
    }

    @Override
    public NettyOutbound send(Publisher<? extends ByteBuf> dataStream) {
        return delegator.send(dataStream);
    }

    @Override
    public NettyOutbound send(Publisher<? extends ByteBuf> dataStream, Predicate<ByteBuf> predicate) {
        return delegator.send(dataStream, predicate);
    }

    @Override
    public NettyOutbound sendObject(Object message) {
        return delegator.sendObject(message);
    }

    @Override
    public NettyOutbound sendObject(Publisher<?> dataStream, Predicate<Object> predicate) {
        return delegator.sendObject(dataStream, predicate);
    }

    @Override
    public <S> NettyOutbound sendUsing(
        Callable<? extends S> sourceInput, BiFunction<? super Connection, ? super S, ?> mappedInput, 
        Consumer<? super S> sourceCleanup
    ) {
        return delegator.sendUsing(sourceInput, mappedInput, sourceCleanup);
    }

    @Override
    public Map<CharSequence, List<Cookie>> allCookies() {
        return delegator.allCookies();
    }

    @Override
    public Map<CharSequence, Set<Cookie>> cookies() {
        return delegator.cookies();
    }

    @Override
    public String fullPath() {
        return delegator.fullPath();
    }

    @Override
    public boolean isKeepAlive() {
        return delegator.isKeepAlive();
    }

    @Override
    public boolean isWebsocket() {
        return delegator.isWebsocket();
    }

    @Override
    public HttpMethod method() {
        return delegator.method();
    }

    @Override
    public String requestId() {
        return delegator.requestId();
    }

    @Override
    public String uri() {
        return delegator.uri();
    }

    @Override
    public HttpVersion version() {
        return delegator.version();
    }

}
