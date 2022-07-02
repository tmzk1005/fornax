package zk.fornax.http.framework;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CrudService<P extends Po<D>, D extends Dto> {

    Repository<P> getRepository();

    Class<P> getPoClass();

    default P mapDtoToPo(D dtoInstance) {
        return Po.fromDto(getPoClass(), dtoInstance);
    }

    default Flux<P> get(FilterContext filterContext) {
        return getRepository().find(filterContext);
    }

    default Mono<P> getById(String id) {
        return getRepository().findById(id);
    }

    default Mono<Long> count(FilterContext filterContext) {
        return getRepository().count(filterContext);
    }

    default Mono<P> create(D dtoInstance) {
        P poInstance = mapDtoToPo(dtoInstance);
        poInstance.setId(null);
        return getRepository().insert(poInstance);
    }

    default Mono<P> update(String id, D dtoInstance) {
        P poInstance = mapDtoToPo(dtoInstance);
        poInstance.setId(id);
        return getRepository().save(poInstance);
    }

    default Mono<Void> delete(P poInstance) {
        return getRepository().delete(poInstance);
    }

    default Mono<Void> deleteById(String id) {
        return getRepository().deleteById(id);
    }

}
