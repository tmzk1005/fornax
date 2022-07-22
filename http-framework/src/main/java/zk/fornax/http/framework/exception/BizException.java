package zk.fornax.http.framework.exception;

import zk.fornax.http.framework.annotation.ResponseStatus;

@ResponseStatus(code = 200)
public class BizException extends RequestRelatedException {

    public BizException(int code, String message) {
        super(code, message);
    }

    public BizException(String message) {
        super(message);
    }

}
