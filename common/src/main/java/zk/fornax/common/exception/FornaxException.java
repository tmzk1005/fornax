package zk.fornax.common.exception;

public class FornaxException extends Exception {

    public FornaxException(String message) {
        super(message);
    }

    public FornaxException(Throwable throwable) {
        super(throwable);
    }

    public FornaxException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
