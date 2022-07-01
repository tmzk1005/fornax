package zk.fornax.http.core;

import reactor.core.publisher.Flux;

import zk.fornax.common.httpapi.HttpApi;

public interface HttpApiLocator extends Server {

    Flux<HttpApi> getHttpApis(String path);

    @Override
    default void startup() {
    }

    @Override
    default void shutdown() {
    }

}
