package zk.fornax.manager.controller;

import reactor.core.publisher.Mono;

import zk.fornax.common.httpapi.HttpMethod;
import zk.fornax.http.framework.annotation.Controller;
import zk.fornax.http.framework.annotation.RequestBody;
import zk.fornax.http.framework.annotation.RequestParam;
import zk.fornax.http.framework.annotation.Route;
import zk.fornax.http.framework.validate.PageNum;
import zk.fornax.http.framework.validate.PageSize;
import zk.fornax.manager.bean.PageData;
import zk.fornax.manager.bean.dto.AppDto;
import zk.fornax.manager.bean.vo.AppVo;
import zk.fornax.manager.service.AppService;
import zk.fornax.manager.service.ServiceFactory;

@Controller
public class AppController {

    private final AppService appService = ServiceFactory.get(AppService.class);

    @Route(method = HttpMethod.POST)
    public Mono<AppVo> createApp(@RequestBody AppDto appDto) {
        return appService.createApp(appDto).map(AppVo::fromPo);
    }

    @Route
    public Mono<PageData<AppVo>> listApps(
        @PageNum @RequestParam(name = "pageNum", required = false, defaultValue = "1") int pageNum,
        @PageSize @RequestParam(name = "pageNum", required = false, defaultValue = "10") int pageSize
    ) {
        return appService.listApps(pageNum, pageSize).map(page -> page.map(AppVo::fromPo));
    }

}
