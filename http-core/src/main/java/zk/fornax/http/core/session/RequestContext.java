package zk.fornax.http.core.session;

import java.util.Map;

import zk.fornax.http.core.exchange.WebExchange;

public interface RequestContext {

    WebExchange getWebExchange();

    Map<String, Object> getAttributes();

}
