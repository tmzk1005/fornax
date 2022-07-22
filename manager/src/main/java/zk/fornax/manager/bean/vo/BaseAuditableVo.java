package zk.fornax.manager.bean.vo;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import zk.fornax.common.utils.JsonUtil;
import zk.fornax.http.framework.Po;
import zk.fornax.http.framework.Vo;
import zk.fornax.manager.bean.po.Auditable;
import zk.fornax.manager.bean.po.User;

@Getter
@Setter
public abstract class BaseAuditableVo<P extends Po<?>> implements Vo<P> {

    protected UserId createdBy;

    protected UserId lastModifiedBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JsonUtil.DEFAULT_DATE_TIME_PATTERN)
    protected Instant createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JsonUtil.DEFAULT_DATE_TIME_PATTERN)
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
