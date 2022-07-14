package zk.fornax.gateway.filter;

import java.util.List;
import java.util.Map;

import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerResponse;

import zk.fornax.common.httpapi.BackendType;
import zk.fornax.common.httpapi.HttpApi;
import zk.fornax.common.httpapi.MockBackend;
import zk.fornax.http.core.exchange.WebExchange;
import zk.fornax.http.core.filter.HttpApiFilter;
import zk.fornax.http.core.filter.HttpFilterChain;

public class MockHttpApiFilter implements HttpApiFilter {

    @Override
    public Mono<Void> filter(WebExchange webExchange, HttpFilterChain chain) {
        final HttpApi httpApi = webExchange.getHttpApi();
        if (httpApi.getBackendType() != BackendType.MOCK) {
            return chain.filter(webExchange);
        }
        final MockBackend mockBackend = httpApi.getMockBackend();
        final HttpServerResponse response = webExchange.getResponse();
        response.status(mockBackend.getStatusCode());
        for (Map.Entry<String, List<String>> entry : mockBackend.getHeaders().entrySet()) {
            for (String value : entry.getValue()) {
                response.addHeader(entry.getKey(), value);
            }
        }
        return response.sendString(Mono.just(mockBackend.getBody())).then();
    }

}
