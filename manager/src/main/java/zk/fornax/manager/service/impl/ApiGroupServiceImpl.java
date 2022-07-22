package zk.fornax.manager.service.impl;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import zk.fornax.manager.bean.dto.ApiGroupDto;
import zk.fornax.manager.bean.po.ApiGroupEntity;
import zk.fornax.manager.db.mangodb.MongoFilter;
import zk.fornax.manager.repository.ApiGroupRepository;
import zk.fornax.manager.repository.RepositoryFactory;
import zk.fornax.manager.service.ApiGroupService;

public class ApiGroupServiceImpl implements ApiGroupService {

    private final ApiGroupRepository apiGroupRepository = RepositoryFactory.get(ApiGroupRepository.class);

    @Override
    public Mono<ApiGroupEntity> create(ApiGroupDto apiGroupDto) {
        return apiGroupRepository.insert(new ApiGroupEntity().initFromDto(apiGroupDto));
    }

    @Override
    public Flux<ApiGroupEntity> listApiGroups(int pageNum, int pageSize) {
        return apiGroupRepository.findAndFilterByOwner(MongoFilter.empty().page(pageNum, pageSize));
    }

}
