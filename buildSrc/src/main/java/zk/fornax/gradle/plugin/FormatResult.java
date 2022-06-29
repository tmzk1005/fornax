package zk.fornax.gradle.plugin;

public enum FormatResult {

    UNCHANGED,
    SUCCEED,
    FAILED;

    private String message;

    public FormatResult withMessage(String message) {
        this.message = message;
        return this;
    }

    public String getMessage() {
        return message;
    }

}
