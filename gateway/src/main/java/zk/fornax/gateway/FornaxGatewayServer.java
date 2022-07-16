package zk.fornax.gateway;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import reactor.netty.http.client.HttpClient;

import zk.fornax.common.httpapi.BackendType;
import zk.fornax.gateway.filter.ExceptionHandleHttpApiFilter;
import zk.fornax.gateway.filter.HttpRoutingFilter;
import zk.fornax.gateway.filter.MockHttpApiFilter;
import zk.fornax.gateway.filter.RouteToRequestUrlFilter;
import zk.fornax.gateway.locator.CompositeHttpApiLocator;
import zk.fornax.gateway.locator.GatewayHttpApiLocator;
import zk.fornax.gateway.locator.JsonFileHttpApiLocator;
import zk.fornax.http.core.AbstractHttpServer;
import zk.fornax.http.core.HttpApiLocator;
import zk.fornax.http.core.HttpApiMatcherImpl;
import zk.fornax.http.core.exchange.WebExchange;
import zk.fornax.http.core.handler.ChainBasedWebHandler;
import zk.fornax.http.core.handler.WebHandler;

public class FornaxGatewayServer extends AbstractHttpServer {

    private final GatewayConfigurtion configurtion;

    private WebHandler mockWebHandler;

    private WebHandler httpWebHandler;

    public FornaxGatewayServer(GatewayConfigurtion configurtion) {
        super(configurtion.getServerHost(), configurtion.getServerPort());
        this.configurtion = configurtion;
        this.httpApiMatcher = new HttpApiMatcherImpl();
        configureHttpApiLocator();
        initWebHandlers();
    }

    private void initWebHandlers() {
        final HttpClient httpClient = HttpClient.create();
        ExceptionHandleHttpApiFilter exceptionHandleHttpApiFilter = new ExceptionHandleHttpApiFilter();
        mockWebHandler = new ChainBasedWebHandler(
            List.of(
                exceptionHandleHttpApiFilter,
                new MockHttpApiFilter()
            )
        );
        httpWebHandler = new ChainBasedWebHandler(
            List.of(
                exceptionHandleHttpApiFilter,
                new RouteToRequestUrlFilter(),
                new HttpRoutingFilter(httpClient)
            )
        );
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

    private HttpApiLocator configureHttpApiLocator() {
        List<HttpApiLocator> httpApiLocators = new ArrayList<>();
        String apiJsonFile = configurtion.getApiJsonFile();
        if (Objects.nonNull(apiJsonFile)) {
            JsonFileHttpApiLocator jsonFileHttpApiLocator = new JsonFileHttpApiLocator(configurtion.getApiJsonFile());
            httpApiLocators.add(jsonFileHttpApiLocator);
        }
        httpApiLocators.add(new GatewayHttpApiLocator());
        return new CompositeHttpApiLocator(httpApiLocators.toArray(new HttpApiLocator[0]));
    }

}
