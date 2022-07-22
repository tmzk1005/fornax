package zk.fornax.manager.bean.dto;

import java.util.Collection;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import zk.fornax.common.httpapi.ApiParameter;
import zk.fornax.common.httpapi.AuthenticationType;
import zk.fornax.common.httpapi.BackendType;
import zk.fornax.common.httpapi.CorsStrategy;
import zk.fornax.common.httpapi.HttpBackend;
import zk.fornax.common.httpapi.HttpMethod;
import zk.fornax.common.httpapi.MockBackend;
import zk.fornax.http.framework.Dto;
import zk.fornax.http.framework.validate.NotBlank;
import zk.fornax.http.framework.validate.Pattern;
import zk.fornax.http.framework.validate.Size;
import zk.fornax.manager.utils.Patters;

@Getter
@Setter
public class ApiDto implements Dto {

    @NotBlank(message = "API名称不能为空")
    @Size(min = 1, max = 32, message = "API名不能超过32个字符长度")
    @Pattern(regexp = Patters.IDENTIFIER_ZH, message = "API名称只能包含字母，数字，中文字符和下划线")
    private String name;

    @NotBlank(message = "API版本号不能而为空")
    @Pattern(regexp = Patters.VERSION_NUM, message = "API版本号必须符合形如1.2.3的格式，且每个小版本号不能超过3位数")
    private String version;

    private String description;

    @NotBlank(message = "所属分组ID不能为空")
    private String apiGroupId;

    @NotBlank(message = "认证类型不能为空")
    private AuthenticationType authenticationType;

    @NotBlank(message = "Http请求方法不能为空")
    private Set<HttpMethod> httpMethods;

    @NotBlank(message = "API请求路径不能为空")
    private String path;

    private CorsStrategy corsStrategy;

    @NotBlank(message = "后端类型不能为空")
    private BackendType backendType;

    private MockBackend mockBackend;

    private HttpBackend httpBackend;

    private Collection<ApiParameter> parameters;

}
