package zk.fornax.common.rest;

import lombok.Getter;
import lombok.Setter;

public class RestShuck<T> {

    public static final int CODE_OK = 0;
    public static final int CODE_ERROR = -1;
    public static final String MESSAGE_OK = "操作成功";
    public static final String MESSAGE_ERROR = "服务器内部错误";

    @Setter
    @Getter
    private int code;

    @Setter
    @Getter
    private String message;

    @Getter
    @Setter
    private T data;

    public RestShuck() {
        // 保留默认构造函数, 以备反序列化之需
    }

    public RestShuck(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> RestShuck<T> success(T data) {
        return success(MESSAGE_OK, data);
    }

    public static <T> RestShuck<T> success(String message, T data) {
        return new RestShuck<>(CODE_OK, message, data);
    }

    public static RestShuck<Void> success() {
        return new RestShuck<>(CODE_OK, MESSAGE_OK, null);
    }

    public static RestShuck<Void> fail() {
        return new RestShuck<>(CODE_ERROR, MESSAGE_ERROR, null);
    }

    public static RestShuck<Void> fail(int code, String message) {
        return new RestShuck<>(code, message, null);
    }

    public static RestShuck<Void> fail(String message) {
        return fail(CODE_ERROR, message);
    }

    public static RestShuck<Void> fail(int code) {
        return fail(code, MESSAGE_ERROR);
    }

    public String toJsonNoData() {
        return "{\"code\": " + code + ", \"message\": \"" + message + "\"}";
    }

}
