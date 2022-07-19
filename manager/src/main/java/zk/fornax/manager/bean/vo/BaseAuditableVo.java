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

    protected SimpleUser createdBy;

    protected SimpleUser lastModifiedBy;

    protected Instant createdDate;

    protected Instant lastModifiedDate;

    protected void copyAuditInfo(Auditable<User, Instant> auditable) {
        User rawCreatedBy = auditable.getCreatedBy();
        if (Objects.nonNull(rawCreatedBy) && Objects.nonNull(rawCreatedBy.getId())) {
            setCreatedBy(SimpleUser.from(rawCreatedBy));
        }
        User rawLastModifiedBy = auditable.getLastModifiedBy();
        if (Objects.nonNull(rawLastModifiedBy) && Objects.nonNull(rawLastModifiedBy.getId())) {
            setLastModifiedBy(SimpleUser.from(rawLastModifiedBy));
        }
        setCreatedDate(auditable.getCreatedDate());
        setLastModifiedDate(auditable.getLastModifiedDate());
    }

    @Getter
    private static class SimpleUser implements Serializable {

        private String id;

        private String username;

        private String nickname;

        public static SimpleUser from(User user) {
            SimpleUser simpleUser = new SimpleUser();
            simpleUser.id = user.getId();
            simpleUser.username = user.getUsername();
            simpleUser.nickname = user.getNickname();
            return simpleUser;
        }

    }

}
