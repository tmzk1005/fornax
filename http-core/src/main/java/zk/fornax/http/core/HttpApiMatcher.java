package zk.fornax.http.core;

import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;

import zk.fornax.common.httpapi.HttpApi;

public interface HttpApiMatcher {

    Mono<Boolean> match(HttpServerRequest request, HttpApi httpApi);

}
