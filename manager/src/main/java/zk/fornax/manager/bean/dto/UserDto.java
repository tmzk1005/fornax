package zk.fornax.manager.bean.dto;

import lombok.Getter;
import lombok.Setter;

import zk.fornax.http.framework.Dto;

@Getter
@Setter
public class UserDto implements Dto {

    private String username;

    private String nickname;

    private String password;

    private String email;

    private String phone;

}
