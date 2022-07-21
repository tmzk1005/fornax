package zk.fornax.http.framework.exception;

import lombok.Getter;

import zk.fornax.common.exception.FornaxRuntimeException;

public class BizException extends FornaxRuntimeException {

    @Getter
    private int code = -1;

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(String message) {
        super(message);
    }

}
