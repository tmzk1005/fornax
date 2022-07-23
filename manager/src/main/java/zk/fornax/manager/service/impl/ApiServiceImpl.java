package zk.fornax.manager.service.impl;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import zk.fornax.manager.bean.dto.ApiDto;
import zk.fornax.manager.bean.po.ApiEntity;
import zk.fornax.manager.db.mangodb.MongoFilter;
import zk.fornax.manager.exception.EntityNotExistException;
import zk.fornax.manager.repository.ApiGroupRepository;
import zk.fornax.manager.repository.ApiRepository;
import zk.fornax.manager.repository.RepositoryFactory;
import zk.fornax.manager.service.ApiService;

public class ApiServiceImpl implements ApiService {

    private final ApiRepository apiRepository = RepositoryFactory.get(ApiRepository.class);

    private final ApiGroupRepository apiGroupRepository = RepositoryFactory.get(ApiGroupRepository.class);

    @Override
    public Mono<ApiEntity> createApi(ApiDto apiDto) {
        return apiGroupRepository.findOneById(apiDto.getApiGroupId())
            .switchIfEmpty(Mono.error(new EntityNotExistException("不存在id为" + apiDto.getApiGroupId() + "的API分组")))
            .flatMap(
                apiGroup -> apiRepository.insert(new ApiEntity().initFromDto(apiDto))
                    .map(api -> {
                        api.setGroup(apiGroup);
                        return api;
                    })
            );
    }

    @Override
    public Flux<ApiEntity> listApis(int pageNum, int pageSize) {
        return apiRepository.findAndFilterByOwner(MongoFilter.empty().page(pageNum, pageSize));
    }

}
