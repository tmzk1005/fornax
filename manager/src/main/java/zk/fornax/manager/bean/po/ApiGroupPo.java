package zk.fornax.manager.bean.po;

import lombok.Getter;
import lombok.Setter;

import zk.fornax.manager.bean.dto.ApiGroupDto;

@Getter
@Setter
public class ApiGroupPo extends BaseAuditablePo<ApiGroupDto> {

    private String id;

    private String name;

    private String address;

    private String description;

    @SuppressWarnings("unchecked")
    @Override
    public ApiGroupPo setFromDto(ApiGroupDto dto) {
        name = dto.getName();
        address = dto.getAddress();
        description = dto.getDescription();
        this.id = null;
        return this;
    }

}
