package zk.fornax.manager.controller;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import zk.fornax.common.httpapi.HttpMethod;
import zk.fornax.http.framework.annotation.Controller;
import zk.fornax.http.framework.annotation.RequestBody;
import zk.fornax.http.framework.annotation.RequestParam;
import zk.fornax.http.framework.annotation.Route;
import zk.fornax.http.framework.validate.PageNum;
import zk.fornax.http.framework.validate.PageSize;
import zk.fornax.manager.bean.dto.ApiGroupDto;
import zk.fornax.manager.bean.vo.ApiGroupVo;
import zk.fornax.manager.service.ApiGroupService;
import zk.fornax.manager.service.ServiceFactory;

@Controller
public class ApiGroupController {

    private final ApiGroupService apiGroupService = ServiceFactory.get(ApiGroupService.class);

    @Route(method = HttpMethod.POST)
    public Mono<ApiGroupVo> createApiGroup(@RequestBody ApiGroupDto apiGroupDto) {
        return apiGroupService.create(apiGroupDto).map(ApiGroupVo::fromPo);
    }

    @Route
    public Flux<ApiGroupVo> listApiGroups(
        @PageNum @RequestParam(name = "pageNum", required = false, defaultValue = "1") int pageNum,
        @PageSize @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize
    ) {
        return apiGroupService.listApiGroups(pageNum, pageSize).map(ApiGroupVo::fromPo);
    }

}
