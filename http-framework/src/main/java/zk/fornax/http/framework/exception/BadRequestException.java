package zk.fornax.http.framework.exception;

import zk.fornax.common.exception.FornaxRuntimeException;
import zk.fornax.http.framework.annotation.ResponseStatus;

@ResponseStatus(code = 400)
public class BadRequestException extends FornaxRuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
