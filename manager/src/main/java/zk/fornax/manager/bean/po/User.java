package zk.fornax.manager.bean.po;

import java.security.Principal;
import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import org.bson.BsonType;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonRepresentation;

import zk.fornax.http.framework.security.Pbkdf2PasswordEncoder;
import zk.fornax.manager.bean.Role;
import zk.fornax.manager.bean.dto.UserDto;
import zk.fornax.manager.db.mangodb.Document;
import zk.fornax.manager.db.mangodb.Index;

@Getter
@Setter
@Document
@Index(name = "User-username-index", unique = true, def = "{\"username\": 1}")
public class User extends BaseAuditableEntity<UserDto> implements Principal {

    @BsonId
    @BsonRepresentation(BsonType.OBJECT_ID)
    private String id;

    private String username;

    private String nickname;

    private String password;

    private boolean enabled = true;

    private String email;

    private String phone;

    private Role role = Role.NORMAL_USER;

    private Instant unlockTime = Instant.ofEpochMilli(0);

    @SuppressWarnings("unchecked")
    @Override
    public User initFromDto(UserDto dto) {
        this.id = null;
        this.username = dto.getUsername();
        this.nickname = dto.getNickname();
        this.password = Pbkdf2PasswordEncoder.getDefaultInstance().encode(dto.getPassword());
        this.email = dto.getEmail();
        this.phone = dto.getPhone();
        return this;
    }

    @Override
    public String getName() {
        return username;
    }

}
