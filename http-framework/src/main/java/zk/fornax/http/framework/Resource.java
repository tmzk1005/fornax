package zk.fornax.http.framework;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface Resource<P extends Po<D>, D extends Dto, V extends Vo<P>> {

    CrudService<P, D> getCrudService();

    Class<V> getVoClass();

    default V mapPoToVo(P poInstance) {
        return Vo.fromPo(getVoClass(), poInstance);
    }

    default Flux<V> get(FilterContext filterContext) {
        return getCrudService().get(filterContext).map(this::mapPoToVo);
    }

    default Mono<V> getById(String id) {
        return getCrudService().getById(id).map(this::mapPoToVo);
    }

    default Mono<V> create(D dtoInstance) {
        return getCrudService().create(dtoInstance).map(this::mapPoToVo);
    }

    default Mono<V> update(String id, D dtoInstance) {
        return getCrudService().update(id, dtoInstance).map(this::mapPoToVo);
    }

    default Mono<Void> deleteById(String id) {
        return getCrudService().deleteById(id);
    }

    default Mono<Long> count(FilterContext filterContext) {
        return getCrudService().count(filterContext);
    }

}
