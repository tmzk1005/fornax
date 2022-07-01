package zk.fornax.http.core;

import reactor.core.publisher.Flux;

import zk.fornax.common.httpapi.HttpApi;

public interface HttpApiLocator {

    Flux<HttpApi> getHttpApis(String path);

}
