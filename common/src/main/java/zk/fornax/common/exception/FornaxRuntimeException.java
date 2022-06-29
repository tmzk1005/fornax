package zk.fornax.common.exception;

public class FornaxRuntimeException extends Exception {

    public FornaxRuntimeException(String message) {
        super(message);
    }

    public FornaxRuntimeException(Throwable throwable) {
        super(throwable);
    }

    public FornaxRuntimeException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
