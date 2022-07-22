package zk.fornax.manager.bean.vo;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

import zk.fornax.http.framework.Po;
import zk.fornax.http.framework.Vo;
import zk.fornax.manager.bean.po.Auditable;
import zk.fornax.manager.bean.po.User;

@Getter
@Setter
public abstract class BaseAuditableVo<P extends Po<?>> implements Vo<P> {

    protected UserId createdBy;

    protected UserId lastModifiedBy;

    protected Instant createdDate;

    protected Instant lastModifiedDate;

    protected void copyAuditInfo(Auditable<User, Instant> auditable) {
        User rawCreatedBy = auditable.getCreatedBy();
        if (Objects.nonNull(rawCreatedBy) && Objects.nonNull(rawCreatedBy.getId())) {
            setCreatedBy(UserId.from(rawCreatedBy));
        }
        User rawLastModifiedBy = auditable.getLastModifiedBy();
        if (Objects.nonNull(rawLastModifiedBy) && Objects.nonNull(rawLastModifiedBy.getId())) {
            setLastModifiedBy(UserId.from(rawLastModifiedBy));
        }
        setCreatedDate(auditable.getCreatedDate());
        setLastModifiedDate(auditable.getLastModifiedDate());
    }

    @Getter
    private static class UserId implements Serializable {

        private String id;

        public static UserId from(User user) {
            UserId simpleUser = new UserId();
            simpleUser.id = user.getId();
            return simpleUser;
        }

    }

}
