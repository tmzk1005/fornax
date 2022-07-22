package zk.fornax.http.framework.exception;

import zk.fornax.http.framework.annotation.ResponseStatus;

@ResponseStatus(code = 403)
public class AccessDeniedException extends RequestRelatedException {

    public AccessDeniedException(String message) {
        super(message);
    }

}
