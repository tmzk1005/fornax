package zk.fornax.gateway.locator;

import lombok.Getter;
import reactor.core.publisher.Flux;

import zk.fornax.common.httpapi.HttpApi;
import zk.fornax.http.core.HttpApiLocator;

public class CompositeHttpApiLocator implements HttpApiLocator {

    @Getter
    private final Flux<HttpApiLocator> delegates;

    public CompositeHttpApiLocator(HttpApiLocator... httpApiLocators) {
        this(Flux.fromArray(httpApiLocators));
    }

    public CompositeHttpApiLocator(Flux<HttpApiLocator> delegates) {
        this.delegates = delegates;
    }

    @Override
    public Flux<HttpApi> getHttpApis(String path) {
        return this.delegates.flatMapSequential(httpApiLocator -> httpApiLocator.getHttpApis(path));
    }

    @Override
    public void startup() {
        delegates.doOnNext(httpApiLocator -> httpApiLocator.startup()).subscribe();
    }

    @Override
    public void shutdown() {
        delegates.doOnNext(httpApiLocator -> httpApiLocator.shutdown()).subscribe();
    }

}
