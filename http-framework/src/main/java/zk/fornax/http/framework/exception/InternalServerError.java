package zk.fornax.http.framework.exception;

import io.netty.handler.codec.http.HttpResponseStatus;

import zk.fornax.common.exception.FornaxRuntimeException;
import zk.fornax.http.framework.annotation.ResponseStatus;

@ResponseStatus
public class InternalServerError extends FornaxRuntimeException {

    public InternalServerError() {
        this(HttpResponseStatus.INTERNAL_SERVER_ERROR.reasonPhrase());
    }

    public InternalServerError(String message) {
        super(message);
    }

    public InternalServerError(Throwable throwable) {
        super(throwable);
    }

}
