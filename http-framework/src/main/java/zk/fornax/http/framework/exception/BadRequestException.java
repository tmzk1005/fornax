package zk.fornax.http.framework.exception;

import reactor.netty.http.server.HttpServerRequest;

import zk.fornax.http.framework.annotation.ResponseStatus;

@ResponseStatus(code = 400)
public class BadRequestException extends RequestRelatedException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(int code, String message) {
        super(code, message);
    }

    public BadRequestException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public BadRequestException(int code, String message, Throwable throwable) {
        super(code, message, throwable);
    }

    public BadRequestException(String message, HttpServerRequest request) {
        super(message, request);
    }

    public BadRequestException(int code, String message, HttpServerRequest request) {
        super(code, message, request);
    }

    public BadRequestException(String message, Throwable throwable, HttpServerRequest request) {
        super(message, throwable, request);
    }

    public BadRequestException(int code, String message, Throwable throwable, HttpServerRequest request) {
        super(code, message, throwable, request);
    }

}
