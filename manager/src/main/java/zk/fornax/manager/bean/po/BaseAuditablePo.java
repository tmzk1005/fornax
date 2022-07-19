package zk.fornax.manager.bean.po;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

import zk.fornax.http.framework.Dto;

@Getter
@Setter
public abstract class BaseAuditablePo<D extends Dto> implements AuditablePo<User, Instant, D> {

    public static final String CREATED_BY = "createdBy";

    public static final String LAST_MODIFIED_DATE = "lastModifiedDate";

    protected User createdBy;

    protected User lastModifiedBy;

    protected Instant createdDate;

    protected Instant lastModifiedDate;

}
