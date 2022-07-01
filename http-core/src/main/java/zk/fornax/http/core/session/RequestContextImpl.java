package zk.fornax.http.core.session;

import java.util.HashMap;
import java.util.Map;

import zk.fornax.http.core.exchange.WebExchange;

public class RequestContextImpl implements RequestContext {

    private final WebExchange webExchange;

    private final Map<String, Object> attributes = new HashMap<>();

    public RequestContextImpl(WebExchange webExchange) {
        this.webExchange = webExchange;
    }

    @Override
    public WebExchange getWebExchange() {
        return webExchange;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

}
