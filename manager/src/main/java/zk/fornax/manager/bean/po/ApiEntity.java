package zk.fornax.manager.bean.po;

import java.util.Collection;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import org.bson.BsonType;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonRepresentation;

import zk.fornax.common.httpapi.ApiParameter;
import zk.fornax.common.httpapi.ApiStatus;
import zk.fornax.common.httpapi.AuthenticationType;
import zk.fornax.common.httpapi.BackendType;
import zk.fornax.common.httpapi.CorsStrategy;
import zk.fornax.common.httpapi.HttpBackend;
import zk.fornax.common.httpapi.HttpMethod;
import zk.fornax.common.httpapi.MockBackend;
import zk.fornax.manager.bean.dto.ApiDto;
import zk.fornax.manager.db.mangodb.Document;
import zk.fornax.manager.db.mangodb.DocumentReference;
import zk.fornax.manager.db.mangodb.Index;

@Getter
@Setter
@Document(collection = "Api")
@Index(name = "Api-nameVersionGroup-index", unique = true, def = "{\"name\": 1, \"version\": 1, \"group\": 1}")
public class ApiEntity extends BaseAuditableEntity<ApiDto> {

    @BsonId
    @BsonRepresentation(BsonType.OBJECT_ID)
    private String id;

    private String name;

    private String version;

    private String description;

    private Set<HttpMethod> httpMethods;

    private String path;

    private AuthenticationType authenticationType;

    private CorsStrategy corsStrategy;

    @DocumentReference
    private ApiGroupEntity group;

    private Collection<ApiParameter> parameters;

    private BackendType backendType;

    private MockBackend mockBackend;

    private HttpBackend httpBackend;

    private ApiStatus apiStatus = ApiStatus.OFFLINE;

    @SuppressWarnings("unchecked")
    @Override
    public ApiEntity initFromDto(ApiDto dto) {
        this.id = null;
        this.name = dto.getName();
        this.version = dto.getVersion();
        this.description = dto.getDescription();
        this.httpMethods = dto.getHttpMethods();
        this.path = dto.getPath();
        this.authenticationType = dto.getAuthenticationType();
        this.corsStrategy = dto.getCorsStrategy();
        ApiGroupEntity apiGroupEntity = new ApiGroupEntity();
        apiGroupEntity.setId(dto.getApiGroupId());
        this.group = apiGroupEntity;
        this.parameters = dto.getParameters();
        this.backendType = dto.getBackendType();
        this.mockBackend = dto.getMockBackend();
        this.httpBackend = dto.getHttpBackend();
        return this;
    }
    
}
