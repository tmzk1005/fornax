package zk.fornax.http.core.exchange;

import java.util.Map;
import java.util.Set;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.cookie.Cookie;
import lombok.Getter;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClientResponse;
import reactor.util.context.Context;
import reactor.util.context.ContextView;

public class HttpServerResponseDecorator implements HttpClientResponse {

    @Getter
    private HttpClientResponse decorator;

    public HttpServerResponseDecorator(HttpClientResponse decorator) {
        this.decorator = decorator;
    }

    @Override
    public HttpHeaders responseHeaders() {
        return decorator.responseHeaders();
    }

    @Override
    public HttpResponseStatus status() {
        return decorator.status();
    }

    @Override
    public Mono<HttpHeaders> trailerHeaders() {
        return decorator.trailerHeaders();
    }

    @Deprecated
    @Override
    public Context currentContext() {
        return decorator.currentContext();
    }

    @Override
    public ContextView currentContextView() {
        return decorator.currentContextView();
    }

    @Override
    public String[] redirectedFrom() {
        return decorator.redirectedFrom();
    }

    @Override
    public HttpHeaders requestHeaders() {
        return decorator.requestHeaders();
    }

    @Override
    public String resourceUrl() {
        return decorator.resourceUrl();
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

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [delegator=" + getDecorator() + "]";
    }

}
