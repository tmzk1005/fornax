package zk.fornax.gateway.locator;

import reactor.core.publisher.Flux;

import zk.fornax.common.httpapi.HttpApi;
import zk.fornax.http.core.HttpApiLocator;

public class GatewayHttpApiLocator implements HttpApiLocator {

    @Override
    public Flux<HttpApi> getHttpApis(String path) {
        return Flux.empty();
    }

}
