package zk.fornax.http.framework.exception;

import zk.fornax.common.exception.FornaxRuntimeException;

public abstract class BizException extends FornaxRuntimeException {

    protected BizException(String message) {
        super(message);
    }

}
