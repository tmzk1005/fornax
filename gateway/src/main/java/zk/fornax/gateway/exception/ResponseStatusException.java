package zk.fornax.gateway.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Getter;

import zk.fornax.common.exception.FornaxRuntimeException;

public class ResponseStatusException extends FornaxRuntimeException {

    @Getter
    private final int statusCode;

    @Getter
    private final transient HttpResponseStatus httpResponseStatus;

    public ResponseStatusException(HttpResponseStatus httpResponseStatus) {
        this(httpResponseStatus, httpResponseStatus.reasonPhrase());
    }

    public ResponseStatusException(HttpResponseStatus httpResponseStatus, String message) {
        super(message);
        this.httpResponseStatus = httpResponseStatus;
        this.statusCode = httpResponseStatus.code();
    }

    public ResponseStatusException(HttpResponseStatus httpResponseStatus, String message, Throwable throwable) {
        super(message, throwable);
        this.httpResponseStatus = httpResponseStatus;
        this.statusCode = httpResponseStatus.code();
    }

}
