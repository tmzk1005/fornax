package zk.fornax.manager.service.impl;

import reactor.core.publisher.Mono;

import zk.fornax.manager.bean.PageData;
import zk.fornax.manager.bean.dto.AppDto;
import zk.fornax.manager.bean.po.AppEntity;
import zk.fornax.manager.db.mangodb.MongoFilter;
import zk.fornax.manager.repository.AppRepository;
import zk.fornax.manager.repository.RepositoryFactory;
import zk.fornax.manager.service.AppService;

public class AppServiceImpl implements AppService {

    private final AppRepository appRepository = RepositoryFactory.get(AppRepository.class);

    @Override
    public Mono<AppEntity> createApp(AppDto appDto) {
        return appRepository.insert(new AppEntity().initFromDto(appDto));
    }

    @Override
    public Mono<PageData<AppEntity>> listApps(int pageNum, int pageSize) {
        return appRepository.pageFindAndFilterByOwner(MongoFilter.empty().page(pageNum, pageSize));
    }

}
