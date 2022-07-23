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
import zk.fornax.manager.bean.dto.ApiDto;
import zk.fornax.manager.bean.vo.ApiVo;
import zk.fornax.manager.service.ApiService;
import zk.fornax.manager.service.ServiceFactory;

@Controller
public class ApiController {

    private final ApiService apiService = ServiceFactory.get(ApiService.class);

    @Route(method = HttpMethod.POST)
    public Mono<ApiVo> createApi(@RequestBody ApiDto apiDto) {
        return apiService.createApi(apiDto).map(ApiVo::fromPo);
    }

    @Route
    public Mono<PageData<ApiVo>> listApis(
        @PageNum @RequestParam(name = "pageNum", required = false, defaultValue = "1") int pageNum,
        @PageSize @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize
    ) {
        return apiService.listApis(pageNum, pageSize).map(page -> page.map(ApiVo::fromPo));
    }

}
