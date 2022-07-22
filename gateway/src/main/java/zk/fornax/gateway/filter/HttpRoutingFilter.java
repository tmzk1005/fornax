package zk.fornax.gateway.filter;

import java.net.URI;
import java.time.Duration;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpClientResponse;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import zk.fornax.common.httpapi.HttpProtocol;
import zk.fornax.gateway.exception.ResponseStatusException;
import zk.fornax.gateway.util.WebExchangeHelper;
import zk.fornax.http.core.exchange.WebExchange;
import zk.fornax.http.core.filter.HttpApiFilter;
import zk.fornax.http.core.filter.HttpFilterChain;

@Log4j2
public class HttpRoutingFilter implements HttpApiFilter {

    private final HttpClient httpClient;

    public HttpRoutingFilter(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public Mono<Void> filter(WebExchange webExchange, HttpFilterChain chain) {
        final URI requestUrl = webExchange.getRequiredAttribute(WebExchangeHelper.GATEWAY_REQUEST_URL_ATTR);
        String scheme = requestUrl.getScheme();
        if ((!HttpProtocol.HTTP.name().equalsIgnoreCase(scheme) && !HttpProtocol.HTTPS.name().equalsIgnoreCase(scheme))) {
            return chain.filter(webExchange);
        }
        HttpServerRequest serverRequest = webExchange.getRequest();
        final String url = requestUrl.toASCIIString();
        Flux<HttpClientResponse> responseFlux = httpClient
            .headers(headers -> {
                headers.add(serverRequest.requestHeaders());
                headers.remove(HttpHeaderNames.HOST);
            })
            .request(serverRequest.method())
            .uri(url)
            .send(serverRequest.receive())
            .responseConnection((httpClientResponse, connection) -> {
                HttpServerResponse serverResponse = webExchange.getResponse();
                return serverResponse.status(httpClientResponse.status())
                    .headers(httpClientResponse.responseHeaders())
                    .send(connection.inbound().receive().retain())
                    .then()
                    .thenReturn(httpClientResponse);
            });
        int timeoutSeconds = webExchange.getHttpApi().getHttpBackend().getTimeoutSeconds();
        if (timeoutSeconds > 0) {
            Duration duration = Duration.ofSeconds(timeoutSeconds);
            responseFlux = responseFlux
                .timeout(
                    duration,
                    Mono.error(new ResponseStatusException(HttpResponseStatus.GATEWAY_TIMEOUT, "上游服务超时"))
                )
                .doOnError(
                    throwable -> log.error(
                        "Timeout! Upstream server did not finish response in {} seconds: {} {}",
                        timeoutSeconds, serverRequest.method(), url
                    )
                );
        }
        return responseFlux.then(chain.filter(webExchange));
    }

}
