package zk.fornax.manager.bean.po;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.BsonType;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonRepresentation;

import zk.fornax.manager.bean.dto.AppDto;
import zk.fornax.manager.db.mangodb.Document;
import zk.fornax.manager.db.mangodb.Index;

@Getter
@Setter
@ToString
@Document(collection = "App")
@Index(name = "App-name-index", unique = true, def = "{\"name\": 1}")
public class AppEntity extends BaseAuditableEntity<AppDto> {

    @BsonId
    @BsonRepresentation(BsonType.OBJECT_ID)
    private String id;

    private String name;

    private String description;

    private String key;

    private String secret;

    @SuppressWarnings("unchecked")
    @Override
    public AppEntity initFromDto(AppDto dto) {
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.key = UUID.randomUUID().toString();
        this.secret = UUID.randomUUID().toString();
        return this;
    }

}
