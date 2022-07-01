package zk.fornax.common.httpapi;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class HttpApi {

    private String id;

    private String version;

    private String groupDomain;

    private Set<HttpMethod> httpMethods;

    private String path;

    private CorsStrategy corsStrategy;

    private IpFilterRuleConf ipFilterRuleConf;

    private BackendType backendType;

    private MockBackend mockBackend;

    private HttpBackend httpBackend;

    private ApiStatus apiStatus;

    private long lastModifiedTimestamp;

    private final Map<String, Object> extensions = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <T> T getExtensionInfo(String key) {
        return (T) extensions.get(key);
    }

}
