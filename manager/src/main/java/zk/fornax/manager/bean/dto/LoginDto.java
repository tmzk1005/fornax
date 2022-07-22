package zk.fornax.manager.bean.dto;

import lombok.Getter;
import lombok.Setter;

import zk.fornax.http.framework.validate.NotBlank;
import zk.fornax.http.framework.validate.Validatable;

@Getter
@Setter
public class LoginDto implements Validatable {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

}
