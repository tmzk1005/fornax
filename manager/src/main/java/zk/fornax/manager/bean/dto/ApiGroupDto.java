package zk.fornax.manager.bean.dto;

import lombok.Getter;
import lombok.Setter;

import zk.fornax.http.framework.Dto;

@Getter
@Setter
public class ApiGroupDto implements Dto {

    private String name;

    private String address;

    private String description;

}
