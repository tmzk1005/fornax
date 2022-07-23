package zk.fornax.manager.service;

import reactor.core.publisher.Mono;

import zk.fornax.manager.bean.PageData;
import zk.fornax.manager.bean.Role;
import zk.fornax.manager.bean.dto.AppDto;
import zk.fornax.manager.bean.po.AppEntity;
import zk.fornax.manager.security.HasRole;

public interface AppService {

    @HasRole({ Role.SYSTEM_ADMIN, Role.NORMAL_USER })
    Mono<PageData<AppEntity>> listApps(int pageNum, int pageSize);

    @HasRole(Role.NORMAL_USER)
    Mono<AppEntity> createApp(AppDto appDto);

}
