package zk.fornax.manager.bean.vo;

import lombok.Getter;
import lombok.Setter;

import zk.fornax.manager.bean.po.ApiGroupPo;

@Getter
@Setter
public class ApiGroupVo extends BaseAuditableVo<ApiGroupPo> {

    private String id;

    private String name;

    private String address;

    private String description;

    @SuppressWarnings("unchecked")
    @Override
    public ApiGroupVo setFromPo(ApiGroupPo poInstance) {
        this.id = null;
        this.name = poInstance.getName();
        this.address = poInstance.getAddress();
        this.description = poInstance.getAddress();
        copyAuditInfo(poInstance);
        return this;
    }

}
