package zk.fornax.manager.utils;

public final class Patters {

    private Patters() {
    }

    public static final String IDENTIFIER = "^[a-zA-Z0-9_]+$";

    public static final String IDENTIFIER_ZH = "^[a-zA-Z0-9_\u4E00-\u9FA5]+$";
    public static final String DOMAIN_PART = "([a-zA-Z0-9]|[a-zA-Z0-9][-a-zA-Z0-9]{0,61}[a-zA-Z0-9])";

    public static final String DOMAIN = "^(?=^.{3,255}$)" + DOMAIN_PART + "(\\." + DOMAIN_PART + ")+$";

    public static final String VERSION_NUM = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$";

}
