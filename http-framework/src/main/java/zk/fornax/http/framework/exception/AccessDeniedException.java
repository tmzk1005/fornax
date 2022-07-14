package zk.fornax.http.framework.exception;

import zk.fornax.common.exception.FornaxRuntimeException;
import zk.fornax.http.framework.annotation.ResponseStatus;

@ResponseStatus(code = 403)
public class AccessDeniedException extends FornaxRuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }

}
