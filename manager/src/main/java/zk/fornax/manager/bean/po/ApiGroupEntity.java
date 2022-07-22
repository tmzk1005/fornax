package zk.fornax.manager.bean.po;

import lombok.Getter;
import lombok.Setter;
import org.bson.BsonType;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonRepresentation;

import zk.fornax.manager.bean.dto.ApiGroupDto;
import zk.fornax.manager.db.mangodb.Document;
import zk.fornax.manager.db.mangodb.Index;

@Getter
@Setter
@Document(collection = "ApiGroup")
@Index(name = "ApiGroup-name-index", unique = true, def = "{\"name\": 1}")
public class ApiGroupEntity extends BaseAuditableEntity<ApiGroupDto> {

    @BsonId
    @BsonRepresentation(BsonType.OBJECT_ID)
    private String id;

    private String name;

    private String address;

    private String description;

    @SuppressWarnings("unchecked")
    @Override
    public ApiGroupEntity initFromDto(ApiGroupDto dto) {
        name = dto.getName();
        address = dto.getAddress();
        description = dto.getDescription();
        this.id = null;
        return this;
    }

}
