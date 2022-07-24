package zk.fornax.manager;

import java.nio.file.Files;
import java.util.List;

import lombok.Getter;

import zk.fornax.common.exception.FornaxRuntimeException;
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

    private static final String API_CONTEXT_PATH = "/fornax/";

    private static final String STATIC_ROOT = "static";

    @Getter
    private final ManagerConfiguration configuration;

    private final WebHandler webHandler = new ChainBasedWebHandler(
        List.of(
            new ExceptionHandleHttpApiFilter(),
            new AuthenticationHttpApiFilter(
                List.of(
                    API_CONTEXT_PATH + "Info",
                    API_CONTEXT_PATH + "User/_login",
                    API_CONTEXT_PATH + "Api/httpApi"
                )
            ),
            new ControllerMethodInvokeHttpFilter()
        )
    );

    public FornaxManagerServer(ManagerConfiguration configuration) {
        super(
            configuration.getServerHost(),
            configuration.getServerPort(),
            new ControllerHttpApiLocator(API_CONTEXT_PATH, FornaxManagerServer.class.getPackageName() + ".controller"),
            new HttpApiMatcherImpl(),
            new WebSessionManagerImpl()
        );
        this.configuration = configuration;
        this.apiContextPath = API_CONTEXT_PATH;
        this.staticResourceRootPath = configuration.getFornaxHome().resolve(STATIC_ROOT).toAbsolutePath().normalize();
        if (Files.notExists(staticResourceRootPath)) {
            throw new FornaxRuntimeException("Static resource directroy " + staticResourceRootPath + " not exists!");
        }
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
