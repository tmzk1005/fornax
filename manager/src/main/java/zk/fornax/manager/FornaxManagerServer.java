package zk.fornax.manager;

import java.util.List;

import zk.fornax.http.core.AbstractHttpServer;
import zk.fornax.http.core.HttpApiMatcherImpl;
import zk.fornax.http.core.exchange.WebExchange;
import zk.fornax.http.core.handler.ChainBasedWebHandler;
import zk.fornax.http.core.handler.WebHandler;
import zk.fornax.http.core.session.WebSessionManagerImpl;
import zk.fornax.http.framework.controller.ControllerHttpApiLocator;
import zk.fornax.http.framework.filter.AuthenticationHttpApiFilter;
import zk.fornax.http.framework.filter.ControllerMethodInvokeHttpFilter;
import zk.fornax.http.framework.filter.ExceptionHandleHttpApiFilter;

public class FornaxManagerServer extends AbstractHttpServer {

    public static final String DEFAULT_HOST = "0.0.0.0";

    public static final int DEFAULT_PORT = 8080;

    private final WebHandler webHandler = new ChainBasedWebHandler(
        List.of(
            new ExceptionHandleHttpApiFilter(),
            new AuthenticationHttpApiFilter(List.of("/Info", "/User/_login")),
            new ControllerMethodInvokeHttpFilter()
        )
    );

    public FornaxManagerServer() {
        super(
            DEFAULT_HOST,
            DEFAULT_PORT,
            new ControllerHttpApiLocator(FornaxManagerServer.class.getPackageName() + ".controller"),
            new HttpApiMatcherImpl(),
            new WebSessionManagerImpl()
        );
    }

    @Override
    protected WebHandler getWebHandler(WebExchange webExchange) {
        return webHandler;
    }

}
