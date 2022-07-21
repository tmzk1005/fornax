package zk.fornax.manager;

import java.util.List;

import lombok.Getter;

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
import zk.fornax.manager.dbinit.DatabaseInit;

public class FornaxManagerServer extends AbstractHttpServer {

    @Getter
    private final ManagerConfiguration configuration;

    private final WebHandler webHandler = new ChainBasedWebHandler(
        List.of(
            new ExceptionHandleHttpApiFilter(),
            new AuthenticationHttpApiFilter(List.of("/Info", "/User/_login")),
            new ControllerMethodInvokeHttpFilter()
        )
    );

    public FornaxManagerServer(ManagerConfiguration configuration) {
        super(
            configuration.getServerHost(),
            configuration.getServerPort(),
            new ControllerHttpApiLocator(FornaxManagerServer.class.getPackageName() + ".controller"),
            new HttpApiMatcherImpl(),
            new WebSessionManagerImpl()
        );
        this.configuration = configuration;
    }

    @Override
    protected WebHandler getWebHandler(WebExchange webExchange) {
        return webHandler;
    }

    @Override
    protected void beforeStart() {
        DatabaseInit.init().subscribe();
    }

}
