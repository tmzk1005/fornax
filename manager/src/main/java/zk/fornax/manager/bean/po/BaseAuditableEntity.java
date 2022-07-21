package zk.fornax.manager.bean.po;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

import zk.fornax.http.framework.Dto;
import zk.fornax.manager.db.mangodb.DocumentReference;

@Getter
@Setter
public abstract class BaseAuditableEntity<D extends Dto> implements AuditableEntity<User, Instant, D> {

    public static final String CREATED_BY = "createdBy";

    public static final String LAST_MODIFIED_DATE = "lastModifiedDate";

    @DocumentReference
    protected User createdBy;

    @DocumentReference
    protected User lastModifiedBy;

    protected Instant createdDate;

    protected Instant lastModifiedDate;

}
