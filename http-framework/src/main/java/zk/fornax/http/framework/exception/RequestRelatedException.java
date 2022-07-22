package zk.fornax.http.framework.exception;

import java.util.Objects;

import lombok.Getter;
import reactor.netty.http.server.HttpServerRequest;

import zk.fornax.common.exception.FornaxRuntimeException;

public class RequestRelatedException extends FornaxRuntimeException {

    public static final int DEFAULT_ERR_CODE = -1;

    @Getter
    protected final transient HttpServerRequest request;

    @Getter
    protected final int code;

    protected RequestRelatedException(String message) {
        this(DEFAULT_ERR_CODE, message);
    }

    protected RequestRelatedException(int code, String message) {
        super(message);
        this.code = code;
        this.request = null;
    }

    protected RequestRelatedException(String message, Throwable throwable) {
        this(DEFAULT_ERR_CODE, message, throwable);
    }

    protected RequestRelatedException(int code, String message, Throwable throwable) {
        this(code, message, throwable, null);
    }

    protected RequestRelatedException(String message, HttpServerRequest request) {
        this(DEFAULT_ERR_CODE, message, request);
    }

    protected RequestRelatedException(int code, String message, HttpServerRequest request) {
        super(message);
        this.code = code;
        this.request = request;
    }

    protected RequestRelatedException(String message, Throwable throwable, HttpServerRequest request) {
        this(DEFAULT_ERR_CODE, message, throwable, request);
    }

    protected RequestRelatedException(int code, String message, Throwable throwable, HttpServerRequest request) {
        super(message, throwable);
        this.code = code;
        this.request = request;
    }

    public String requestInfo() {
        return requestInfo(this.request);
    }

    public static String requestInfo(HttpServerRequest request) {
        if (Objects.isNull(request)) {
            return "";
        }
        return "Request[id=" + request.requestId() + " ,method=" + request.method().name() + ", path=" + request.path() + "]";
    }

}
