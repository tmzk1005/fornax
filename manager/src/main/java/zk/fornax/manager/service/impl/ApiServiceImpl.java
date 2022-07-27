package zk.fornax.manager.service.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import zk.fornax.common.httpapi.ApiStatus;
import zk.fornax.http.framework.exception.AccessDeniedException;
import zk.fornax.manager.bean.PageData;
import zk.fornax.manager.bean.dto.ApiDto;
import zk.fornax.manager.bean.po.ApiEntity;
import zk.fornax.manager.bean.po.ApiGroupEntity;
import zk.fornax.manager.bean.po.BaseAuditableEntity;
import zk.fornax.manager.db.mangodb.MongoFilter;
import zk.fornax.manager.exception.EntityNotExistException;
import zk.fornax.manager.repository.ApiGroupRepository;
import zk.fornax.manager.repository.ApiRepository;
import zk.fornax.manager.repository.RepositoryFactory;
import zk.fornax.manager.security.ContextHelper;
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
    public Mono<PageData<ApiEntity>> listApis(int pageNum, int pageSize) {
        return apiRepository.pageFindAndFilterByOwner(MongoFilter.empty().page(pageNum, pageSize))
            .flatMap(pageData -> {
                final List<ApiEntity> apiEntities = pageData.getData();
                Mono<Map<String, ApiGroupEntity>> apiGroupMapMono = fetchApiGroupInfo(apiEntities);
                Mono<List<ApiEntity>> updatedApiEntitiesMono = apiGroupMapMono.map(apiGroupMap -> {
                    apiEntities.forEach(apiEntity -> apiEntity.setGroup(apiGroupMap.get(apiEntity.getGroup().getId())));
                    return apiEntities;
                });
                return updatedApiEntitiesMono.map(data -> {
                    pageData.setData(data);
                    return pageData;
                });
            });
    }

    @Override
    public Mono<ApiEntity> publishApi(String apiId) {
        return changeApiStatus(apiId, ApiStatus.ONLINE);
    }

    @Override
    public Mono<ApiEntity> offlineApi(String apiId) {
        return changeApiStatus(apiId, ApiStatus.OFFLINE);
    }

    @Override
    public Flux<ApiEntity> listChangedApisAfterLastModifiedTime(long timeMillis) {
        Bson bsonFilter = Filters.gt(BaseAuditableEntity.LAST_MODIFIED_DATE, Instant.ofEpochMilli(timeMillis));
        if (timeMillis == 0L) {
            bsonFilter = Filters.and(bsonFilter, Filters.eq("apiStatus", ApiStatus.ONLINE));
        }
        MongoFilter mongoFilter = MongoFilter.filter(bsonFilter).ascending(BaseAuditableEntity.LAST_MODIFIED_DATE);
        return apiRepository.find(mongoFilter)
            .flatMap(
                apiEntity -> apiGroupRepository.find(MongoFilter.byId(apiEntity.getGroup().getId())).map(apiGroupEntity -> {
                    apiEntity.setGroup(apiGroupEntity);
                    return apiEntity;
                })
            );
    }

    private Mono<ApiEntity> changeApiStatus(String apiId, ApiStatus apiStatus) {
        return apiRepository.findOneById(apiId)
            .switchIfEmpty(Mono.error(new EntityNotExistException("不存在id为" + apiId + "的API")))
            .flatMap(
                apiEntity -> ContextHelper.currentUserOwnEntity(apiEntity).flatMap(
                    isOwner -> {
                        if (Boolean.TRUE.equals(isOwner)) {
                            apiEntity.setApiStatus(apiStatus);
                            return apiRepository.save(apiEntity);
                        } else {
                            throw new AccessDeniedException("没有权限更新id为" + apiId + "的API");
                        }
                    }
                )
            );
    }

    private Mono<Map<String, ApiGroupEntity>> fetchApiGroupInfo(List<ApiEntity> apiEntities) {
        List<String> groupIds = new ArrayList<>();
        for (ApiEntity apiEntity : apiEntities) {
            groupIds.add(apiEntity.getGroup().getId());
        }
        MongoFilter mongoFilter = MongoFilter.byIds(groupIds);
        return apiGroupRepository.find(mongoFilter).collectMap(ApiGroupEntity::getId);
    }

}
