package zk.fornax.manager.exception;

import zk.fornax.http.framework.exception.BizException;

public class EntityNotExistException extends BizException {

    public EntityNotExistException(String message) {
        super(message);
    }

}
