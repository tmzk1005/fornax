package zk.fornax.http.core.exchange;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.multipart.HttpData;
import reactor.core.publisher.Flux;
import reactor.netty.ByteBufFlux;
import reactor.netty.Connection;
import reactor.netty.http.server.HttpServerFormDecoderProvider;
import reactor.netty.http.server.HttpServerRequest;

public class HttpServerRequestDecorator implements HttpServerRequest {

    private final HttpServerRequest decorator;

    public HttpServerRequestDecorator(HttpServerRequest decorator) {
        this.decorator = decorator;
    }

    @Override
    public HttpServerRequest withConnection(Consumer<? super Connection> withConnection) {
        return decorator.withConnection(withConnection);
    }

    @Override
    public String param(CharSequence key) {
        return decorator.param(key);
    }

    @Override
    public Map<String, String> params() {
        return decorator.params();
    }

    @Override
    public HttpServerRequest paramsResolver(Function<? super String, Map<String, String>> paramsResolver) {
        return decorator.paramsResolver(paramsResolver);
    }

    @Override
    public boolean isFormUrlencoded() {
        return decorator.isFormUrlencoded();
    }

    @Override
    public boolean isMultipart() {
        return decorator.isMultipart();
    }

    @Override
    public Flux<HttpData> receiveForm() {
        return decorator.receiveForm();
    }

    @Override
    public Flux<HttpData> receiveForm(Consumer<HttpServerFormDecoderProvider.Builder> formDecoderBuilder) {
        return decorator.receiveForm(formDecoderBuilder);
    }

    @Override
    public InetSocketAddress hostAddress() {
        return decorator.hostAddress();
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return decorator.remoteAddress();
    }

    @Override
    public HttpHeaders requestHeaders() {
        return decorator.requestHeaders();
    }

    @Override
    public String scheme() {
        return decorator.scheme();
    }

    @Override
    public ByteBufFlux receive() {
        return decorator.receive();
    }

    @Override
    public Flux<?> receiveObject() {
        return decorator.receiveObject();
    }

    @Override
    public Map<CharSequence, List<Cookie>> allCookies() {
        return decorator.allCookies();
    }

    @Override
    public Map<CharSequence, Set<Cookie>> cookies() {
        return decorator.cookies();
    }

    @Override
    public String fullPath() {
        return decorator.fullPath();
    }

    @Override
    public String requestId() {
        return decorator.requestId();
    }

    @Override
    public boolean isKeepAlive() {
        return decorator.isKeepAlive();
    }

    @Override
    public boolean isWebsocket() {
        return decorator.isWebsocket();
    }

    @Override
    public HttpMethod method() {
        return decorator.method();
    }

    @Override
    public String uri() {
        return decorator.uri();
    }

    @Override
    public HttpVersion version() {
        return decorator.version();
    }

}
