package zk.fornax.manager.bean.vo;

import lombok.Getter;
import lombok.Setter;

import zk.fornax.manager.bean.po.AppEntity;

@Getter
@Setter
public class AppVo extends BaseAuditableVo<AppEntity> {

    private String id;

    private String name;

    private String description;

    private String key;

    private String secret;

    @SuppressWarnings("unchecked")
    @Override
    public AppVo initFromPo(AppEntity appEntity) {
        this.id = appEntity.getId();
        this.name = appEntity.getName();
        this.description = appEntity.getDescription();
        this.key = appEntity.getKey();
        this.secret = appEntity.getSecret();
        copyAuditInfo(appEntity);
        return this;
    }

    public static AppVo fromPo(AppEntity appEntity) {
        return new AppVo().initFromPo(appEntity);
    }

}
