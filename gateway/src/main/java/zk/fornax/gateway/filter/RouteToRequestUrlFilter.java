package zk.fornax.gateway.filter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import reactor.core.publisher.Mono;

import zk.fornax.common.exception.FornaxRuntimeException;
import zk.fornax.common.httpapi.HttpApi;
import zk.fornax.common.httpapi.HttpBackend;
import zk.fornax.gateway.util.WebExchangeHelper;
import zk.fornax.http.core.exchange.WebExchange;
import zk.fornax.http.core.filter.HttpApiFilter;
import zk.fornax.http.core.filter.HttpFilterChain;

public class RouteToRequestUrlFilter implements HttpApiFilter {

    @Override
    public Mono<Void> filter(WebExchange webExchange, HttpFilterChain chain) {
        HttpApi httpApi = webExchange.getHttpApi();
        HttpBackend httpBackend = httpApi.getHttpBackend();
        if (Objects.isNull(httpBackend)) {
            return chain.filter(webExchange);
        }
        URI uri;
        try {
            URI rawRequestUri = new URI(webExchange.getRequest().uri());
            uri = new URI(
                httpBackend.getHttpProtocol().name().toLowerCase(), rawRequestUri.getRawUserInfo(),
                httpBackend.getHost(), httpBackend.getPort(), httpBackend.getPath(),
                rawRequestUri.getRawQuery(), rawRequestUri.getRawFragment()
            );
        } catch (URISyntaxException uriSyntaxException) {
            throw new FornaxRuntimeException(uriSyntaxException);
        }
        webExchange.getAttributes().put(WebExchangeHelper.GATEWAY_REQUEST_URL_ATTR, uri);
        return chain.filter(webExchange);
    }

}
