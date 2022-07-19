package zk.fornax.gateway;

import zk.fornax.http.core.Server;
import zk.fornax.http.core.ServerBootstrap;

public class FornaxGatewayServerBootstrap extends ServerBootstrap<GatewayConfigurtion> {

    public static final String APP_NAME = "fornax-gateway";

    private FornaxGatewayServerBootstrap(String[] bootstrapArgs) {
        super(bootstrapArgs, new GatewayConfigurtion(), APP_NAME);
    }

    @Override
    protected Server newServerInstance() {
        return new FornaxGatewayServer(configuration);
    }

    public static void main(String[] args) {
        new FornaxGatewayServerBootstrap(args).bootstrap();
    }

}
