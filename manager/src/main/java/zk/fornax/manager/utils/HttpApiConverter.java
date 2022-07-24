package zk.fornax.manager.utils;

import zk.fornax.common.httpapi.HttpApi;
import zk.fornax.manager.bean.po.ApiEntity;

public class HttpApiConverter {

    private HttpApiConverter() {
    }
    
    public static HttpApi convert(ApiEntity apiEntity) {
        HttpApi httpApi = new HttpApi();
        httpApi.setId(apiEntity.getId());
        httpApi.setVersion(apiEntity.getVersion());
        httpApi.setGroupDomain(apiEntity.getGroup().getAddress());
        httpApi.setHttpMethods(apiEntity.getHttpMethods());
        httpApi.setPath(apiEntity.getPath());
        httpApi.setCorsStrategy(apiEntity.getCorsStrategy());
        httpApi.setBackendType(apiEntity.getBackendType());
        httpApi.setMockBackend(apiEntity.getMockBackend());
        httpApi.setHttpBackend(apiEntity.getHttpBackend());
        httpApi.setApiStatus(apiEntity.getApiStatus());
        httpApi.setLastModifiedTimestamp(apiEntity.getLastModifiedDate().toEpochMilli());
        return httpApi;
    }

}
