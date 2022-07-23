package zk.fornax.manager.bean.vo;

import lombok.Getter;
import lombok.Setter;

import zk.fornax.manager.bean.po.ApiGroupEntity;

@Getter
@Setter
public class ApiGroupVo extends BaseAuditableVo<ApiGroupEntity> {

    private String id;

    private String name;

    private String address;

    private String description;

    @SuppressWarnings("unchecked")
    @Override
    public ApiGroupVo initFromPo(ApiGroupEntity poInstance) {
        this.id = poInstance.getId();
        this.name = poInstance.getName();
        this.address = poInstance.getAddress();
        this.description = poInstance.getAddress();
        copyAuditInfo(poInstance);
        return this;
    }

    public static ApiGroupVo fromPo(ApiGroupEntity apiGroupEntity) {
        return new ApiGroupVo().initFromPo(apiGroupEntity);
    }

}
