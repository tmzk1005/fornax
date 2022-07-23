package zk.fornax.http.core;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;

import io.netty.handler.codec.http.HttpHeaderNames;
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
import zk.fornax.http.core.mime.MimeTypesHelper;
import zk.fornax.http.core.session.ReactiveRequestContextHolder;
import zk.fornax.http.core.session.RequestContext;
import zk.fornax.http.core.session.RequestContextImpl;
import zk.fornax.http.core.session.WebSessionManager;

@Log4j2
public abstract class AbstractHttpServer implements Server {

    private static final String STATIC_RESOURCE_PATH_PREFIX = "static/";

    private static final int PATH_START_INDEX = STATIC_RESOURCE_PATH_PREFIX.length();

    protected final String host;

    protected final int port;

    protected HttpApiLocator httpApiLocator;

    protected HttpApiMatcher httpApiMatcher;

    protected WebSessionManager webSessionManager;

    protected DisposableServer disposableServer;

    protected final AtomicBoolean isStartingUp = new AtomicBoolean(false);

    protected final AtomicBoolean isShuttingDown = new AtomicBoolean(false);

    protected final AtomicBoolean started = new AtomicBoolean(false);

    protected Path staticResourceRootPath;

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
        HttpHandler httpHandler = Objects.nonNull(staticResourceRootPath) ? new HttpHandlerSupportStatic() : new HttpHandler();
        try {
            disposableServer = HttpServer.create().host(host).port(port).handle(httpHandler).bindNow();
        } catch (Exception exception) {
            isStartingUp.set(false);
            log.error(this.getClass().getSimpleName() + " start failed.", exception);
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

    private class HttpHandlerSupportStatic extends HttpHandler {

        @Override
        public Mono<Void> apply(HttpServerRequest request, HttpServerResponse response) {
            String path = request.path();
            if (path.startsWith(STATIC_RESOURCE_PATH_PREFIX)) {
                return serveStaticResource(path, response);
            } else {
                return super.apply(request, response);
            }
        }

        public Mono<Void> serveStaticResource(String path, HttpServerResponse response) {
            Path resourcePath = staticResourceRootPath.resolve(path.substring(PATH_START_INDEX)).toAbsolutePath().normalize();
            if (!resourcePath.startsWith(staticResourceRootPath) || Files.notExists(resourcePath)) {
                return response.sendNotFound();
            }
            File file = resourcePath.toFile();
            if (!file.isFile()) {
                return response.sendNotFound();
            }

            long contentLength = file.length();
            response.header(HttpHeaderNames.CONTENT_LENGTH, contentLength + "");

            String suffix = path.substring(path.lastIndexOf(".") + 1);
            String contentType = MimeTypesHelper.getContentTypeBySuffix(suffix);
            if (Objects.nonNull(contentType)) {
                response.header(HttpHeaderNames.CONTENT_TYPE, contentType);
            }
            return Mono.from(response.sendFile(resourcePath));
        }

    }

    private static class NotFoundException extends FornaxRuntimeException {

        public NotFoundException() {
            super("");
        }

    }

}
