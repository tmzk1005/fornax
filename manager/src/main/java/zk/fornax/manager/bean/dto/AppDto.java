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
public class AppDto implements Dto, Validatable {

    @NotBlank(message = "APP名称不能为空")
    @Size(min = 1, max = 32, message = "APP名不能超过32个字符长度")
    @Pattern(regexp = Patters.IDENTIFIER_ZH, message = "APP名称只能包含字母，数字，中文字符和下划线")
    private String name;

    @NotBlank(message = "App说明不能为空")
    @Size(min = 1, max = 2048, message = "APP名不能超过2048个字符长度")
    private String description;

}
