package zk.fornax.manager.bean.dto;

import lombok.Getter;
import lombok.Setter;

import zk.fornax.http.framework.Dto;
import zk.fornax.http.framework.validate.NotBlank;
import zk.fornax.http.framework.validate.Pattern;
import zk.fornax.http.framework.validate.Size;
import zk.fornax.http.framework.validate.Validatable;
import zk.fornax.manager.utils.Patters;

@Getter
@Setter
public class UserDto implements Dto, Validatable {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 1, max = 16, message = "用户名不能超过16个字符长度")
    @Pattern(regexp = Patters.IDENTIFIER_ZH, message = "用户名只能包含字母，数字，中文字符和下划线")
    private String username;

    @NotBlank(message = "用户昵称不能为空")
    @Size(min = 1, max = 16, message = "用户昵称不能超过16个字符长度")
    @Pattern(regexp = Patters.IDENTIFIER_ZH, message = "用户昵称只能包含字母，数字，中文字符和下划线")
    private String nickname;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 32, message = "密码长度不能小于8,不能超过32")
    private String password;

    @Size(max = 64, message = "电子邮件不能超过64个字符长度")
    private String email;

    @Size(max = 64, message = "电话不能超过32个字符长度")
    private String phone;

}
