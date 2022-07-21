package zk.fornax.http.core;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import zk.fornax.common.exception.FornaxRuntimeException;
import zk.fornax.http.core.exchange.WebExchange;
import zk.fornax.http.core.exchange.WebExchangeImpl;
import zk.fornax.http.core.handler.WebHandler;
import zk.fornax.http.core.session.ReactiveRequestContextHolder;
import zk.fornax.http.core.session.RequestContext;
import zk.fornax.http.core.session.RequestContextImpl;
import zk.fornax.http.core.session.WebSessionManager;

@Log4j2
public abstract class AbstractHttpServer implements Server {

    protected final String host;

    protected final int port;

    protected HttpApiLocator httpApiLocator;

    protected HttpApiMatcher httpApiMatcher;

    protected WebSessionManager webSessionManager;

    protected DisposableServer disposableServer;

    protected final AtomicBoolean isStartingUp = new AtomicBoolean(false);

    protected final AtomicBoolean isShuttingDown = new AtomicBoolean(false);

    protected final AtomicBoolean started = new AtomicBoolean(false);

    protected AbstractHttpServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    protected AbstractHttpServer(
        String host, int port, HttpApiLocator httpApiLocator,
        HttpApiMatcher httpApiMatcher, WebSessionManager webSessionManager
    ) {
        this.host = host;
        this.port = port;
        this.httpApiLocator = httpApiLocator;
        this.httpApiMatcher = httpApiMatcher;
        this.webSessionManager = webSessionManager;
    }

    @Override
    public void startup() {
        if (isShuttingDown.get()) {
            return;
        }
        if (started.get()) {
            return;
        }
        boolean canStartup = isStartingUp.compareAndSet(false, true);
        if (!canStartup) {
            return;
        }
        try {
            disposableServer = HttpServer.create().host(host).port(port).handle(new HttpHandler()).bindNow();
        } catch (Throwable throwable) {
            isStartingUp.set(false);
            log.error(this.getClass().getSimpleName() + " start failed.", throwable);
            return;
        }
        beforeStart();
        started.set(true);
        isStartingUp.set(false);
        startSucceed();
        disposableServer.onDispose().block();
    }

    @Override
    public void shutdown() {
        if (isStartingUp.get()) {
            throw new IllegalStateException(this.getClass().getSimpleName() + "is still starting up, can not shutdown!");
        }
        boolean canShuttingDown = started.get() && isShuttingDown.compareAndSet(false, true);
        if (!canShuttingDown) {
            return;
        }
        beforeStop();
        try {
            disposableServer.disposeNow(Duration.ofSeconds(5));
        } catch (Exception exception) {
            log.error("Exception happened while try to stop HttpServer.", exception);
        }
        disposableServer = null;
        isShuttingDown.set(false);
        started.set(false);
        log.info("{} Stopped.", this.getClass().getSimpleName());
    }

    protected void beforeStart() {
        httpApiLocator.startup();
    }

    protected void startSucceed() {
        log.info("{} Started, http service listening on {}:{}", this.getClass().getSimpleName(), host, port);
    }

    protected void beforeStop() {
        httpApiLocator.shutdown();
    }

    protected abstract WebHandler getWebHandler(WebExchange webExchange);

    private class HttpHandler implements BiFunction<HttpServerRequest, HttpServerResponse, Mono<Void>> {

        @Override
        public Mono<Void> apply(HttpServerRequest request, HttpServerResponse response) {
            return httpApiLocator.getHttpApis(request.fullPath())
                .concatMap(
                    httpApi -> Mono.just(httpApi).filterWhen(theApi -> httpApiMatcher.match(request, theApi))
                )
                .onErrorResume(exception -> {
                    log.error("Failed to do request match.", exception);
                    return Mono.empty();
                })
                .switchIfEmpty(Mono.error(new NotFoundException()))
                .next()
                .flatMap(httpApi -> {
                    final WebExchange webExchange = new WebExchangeImpl(request, response, httpApi, webSessionManager);
                    final RequestContext requestContext = new RequestContextImpl(webExchange);
                    return getWebHandler(webExchange)
                        .handle(webExchange)
                        .contextWrite(ReactiveRequestContextHolder.withRequestContext(Mono.just(requestContext)));
                })
                .onErrorResume(throwable -> {
                    if (throwable instanceof NotFoundException) {
                        log.info("Not found: {} {}", request.method(), request.fullPath());
                        return ResponseHelper.sendJson(response, HttpResponseStatus.NOT_FOUND);
                    } else {
                        log.error("Unhandled exception.", throwable);
                        return ResponseHelper.sendJson(response, HttpResponseStatus.INTERNAL_SERVER_ERROR);
                    }
                });
        }

    }

    private static class NotFoundException extends FornaxRuntimeException {

        public NotFoundException() {
            super("");
        }

    }

}
