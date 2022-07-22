package zk.fornax.manager.bean.vo;

import java.util.Collection;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import zk.fornax.common.httpapi.ApiParameter;
import zk.fornax.common.httpapi.ApiStatus;
import zk.fornax.common.httpapi.BackendType;
import zk.fornax.common.httpapi.CorsStrategy;
import zk.fornax.common.httpapi.HttpBackend;
import zk.fornax.common.httpapi.HttpMethod;
import zk.fornax.common.httpapi.MockBackend;
import zk.fornax.manager.bean.po.ApiEntity;

@Getter
@Setter
public class ApiVo extends BaseAuditableVo<ApiEntity> {

    private String id;

    private String name;

    private String version;

    private String description;

    private Set<HttpMethod> httpMethods;

    private String path;

    private Collection<ApiParameter> parameters;

    private CorsStrategy corsStrategy;

    private ApiGroupVo group;

    private BackendType backendType;

    private MockBackend mockBackend;

    private HttpBackend httpBackend;

    private ApiStatus apiStatus;

    @SuppressWarnings("unchecked")
    @Override
    public ApiVo initFromPo(ApiEntity apiEntity) {
        this.id = apiEntity.getId();
        this.name = apiEntity.getName();
        this.version = apiEntity.getVersion();
        this.description = apiEntity.getDescription();
        this.httpMethods = apiEntity.getHttpMethods();
        this.path = apiEntity.getPath();
        this.parameters = apiEntity.getParameters();
        this.corsStrategy = apiEntity.getCorsStrategy();
        this.backendType = apiEntity.getBackendType();
        this.mockBackend = apiEntity.getMockBackend();
        this.httpMethods = apiEntity.getHttpMethods();
        this.apiStatus = apiEntity.getApiStatus();
        this.group = new ApiGroupVo().initFromPo(apiEntity.getGroup());
        this.copyAuditInfo(apiEntity);
        return this;
    }

    public static ApiVo fromPo(ApiEntity apiEntity) {
        return new ApiVo().initFromPo(apiEntity);
    }

}
