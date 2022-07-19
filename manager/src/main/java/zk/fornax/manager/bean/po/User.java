package zk.fornax.manager.bean.po;

import java.security.Principal;
import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

import zk.fornax.http.framework.security.Pbkdf2PasswordEncoder;
import zk.fornax.manager.bean.Role;
import zk.fornax.manager.bean.dto.UserDto;

@Getter
@Setter
public class User extends BaseAuditablePo<UserDto> implements Principal {

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
    public User setFromDto(UserDto dto) {
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
