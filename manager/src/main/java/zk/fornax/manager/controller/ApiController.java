package zk.fornax.manager.controller;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import zk.fornax.common.httpapi.HttpApi;
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
import zk.fornax.manager.utils.HttpApiConverter;

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

    /**
     * 为网关提供增量获取被改变Api信息服务，无需登录
     *
     * @param timeMillis 毫秒时间戳
     * @return Flux<HttpApi>
     */
    @Route(path = "httpApi")
    public Flux<HttpApi> getHttpApis(@RequestParam(name = "timeMillis", required = false, defaultValue = "0") long timeMillis) {
        return apiService.listChangedApisAfterLastModifiedTime(timeMillis).map(HttpApiConverter::convert);
    }

}
