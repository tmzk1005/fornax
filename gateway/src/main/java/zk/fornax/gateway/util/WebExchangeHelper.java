package zk.fornax.gateway.util;

public class WebExchangeHelper {

    private WebExchangeHelper() {
    }

    public static final String GATEWAY_REQUEST_URL_ATTR = qualify("gatewayRequestUrl");

    private static String qualify(String attr) {
        return WebExchangeHelper.class.getName() + "." + attr;
    }

}
