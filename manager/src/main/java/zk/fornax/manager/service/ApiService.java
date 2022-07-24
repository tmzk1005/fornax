package zk.fornax.manager.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import zk.fornax.manager.bean.PageData;
import zk.fornax.manager.bean.Role;
import zk.fornax.manager.bean.dto.ApiDto;
import zk.fornax.manager.bean.po.ApiEntity;
import zk.fornax.manager.security.HasRole;

public interface ApiService {

    @HasRole(Role.NORMAL_USER)
    Mono<ApiEntity> createApi(ApiDto apiDto);

    @HasRole({ Role.SYSTEM_ADMIN, Role.NORMAL_USER })
    Mono<PageData<ApiEntity>> listApis(int pageNum, int pageSize);

    Mono<ApiEntity> publishApi(String apiId);

    Mono<ApiEntity> offlineApi(String apiId);

    Flux<ApiEntity> listChangedApisAfterLastModifiedTime(long timeMillis);

}
