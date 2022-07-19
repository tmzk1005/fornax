package zk.fornax.manager.bean.po;

import java.time.temporal.TemporalAccessor;

public interface Auditable<U, T extends TemporalAccessor> {

    U getCreatedBy();

    void setCreatedBy(U createdBy);

    T getCreatedDate();

    void setCreatedDate(T creationDate);

    U getLastModifiedBy();

    void setLastModifiedBy(U lastModifiedBy);

    T getLastModifiedDate();

    void setLastModifiedDate(T lastModifiedDate);

}
