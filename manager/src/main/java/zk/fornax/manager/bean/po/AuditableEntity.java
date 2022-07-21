package zk.fornax.manager.bean.po;

import java.time.temporal.TemporalAccessor;

import zk.fornax.http.framework.Dto;
import zk.fornax.http.framework.Po;

public interface AuditableEntity<U, T extends TemporalAccessor, D extends Dto> extends Po<D>, Auditable<U, T> {
}
