package zk.fornax.manager.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import zk.fornax.manager.bean.PageData;
import zk.fornax.manager.bean.Role;
import zk.fornax.manager.bean.dto.ApiGroupDto;
import zk.fornax.manager.bean.po.ApiGroupEntity;
import zk.fornax.manager.security.HasRole;

public interface ApiGroupService {

    @HasRole(Role.NORMAL_USER)
    Mono<ApiGroupEntity> create(ApiGroupDto apiGroupDto);

    Mono<PageData<ApiGroupEntity>> listApiGroups(int pageNum, int pageSize);

    Flux<ApiGroupEntity> searchApiGroups(String text);

}
