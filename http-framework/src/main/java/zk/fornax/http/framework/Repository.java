package zk.fornax.http.framework;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface Repository<P extends Po<?>> {

    Flux<P> find(FilterContext filterContext);

    Mono<P> findOne(FilterContext filterContext);

    default Mono<P> findById(String id) {
        new FilterContext().withId(id);
        return findOne(new FilterContext().withId(id));
    }

    Mono<Long> count(FilterContext filterContext);

    Mono<P> insert(P poInstance);

    Mono<P> save(P poInstance);

    default Mono<Void> delete(P poInstance) {
        return deleteById(poInstance.getId());
    }

    Mono<Void> deleteById(String id);

}
