package zk.fornax.gateway;

import java.util.List;
import java.util.Objects;

import zk.fornax.common.httpapi.BackendType;
import zk.fornax.gateway.locator.GatewayHttpApiLocator;
import zk.fornax.http.core.AbstractHttpServer;
import zk.fornax.http.core.HttpApiMatcherImpl;
import zk.fornax.http.core.exchange.WebExchange;
import zk.fornax.http.core.handler.ChainBasedWebHandler;
import zk.fornax.http.core.handler.WebHandler;

public class FornaxGatewayServer extends AbstractHttpServer {

    public static final int DEFAULT_PORT = 8000;

    private WebHandler mockWebHandler;

    private WebHandler httpWebHandler;

    public FornaxGatewayServer() {
        super(DEFAULT_PORT, new GatewayHttpApiLocator(), new HttpApiMatcherImpl(), null);
        initWebHandlers();
    }

    private void initWebHandlers() {
        mockWebHandler = new ChainBasedWebHandler(List.of());
        httpWebHandler = new ChainBasedWebHandler(List.of());
    }

    @Override
    protected WebHandler getWebHandler(WebExchange webExchange) {
        if (Objects.isNull(webExchange)) {
            return null;
        }
        BackendType backendType = webExchange.getHttpApi().getBackendType();
        if (backendType == BackendType.MOCK) {
            return mockWebHandler;
        } else if (backendType == BackendType.HTTP) {
            return httpWebHandler;
        }
        return null;
    }

}
