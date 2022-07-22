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
public class ApiGroupDto implements Dto, Validatable {

    @NotBlank(message = "API分组名称不能为空")
    @Size(min = 1, max = 32, message = "API分组名称不能超过32个字符长度")
    @Pattern(regexp = Patters.IDENTIFIER_ZH, message = "API分组名称只能包含字母，数字，中文字符和下划线")
    private String name;

    @NotBlank(message = "API分组域名地址不能为空")
    @Size(min = 1, max = 64, message = "API分组域名地址不能超过64个字符长度")
    @Pattern(regexp = Patters.DOMAIN, message = "API分组域名地址非法")
    private String address;

    @NotBlank(message = "请填写API功能简要说明")
    private String description;

}
