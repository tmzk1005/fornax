package zk.fornax.manager;

import zk.fornax.http.core.Server;
import zk.fornax.http.core.ServerBootstrap;

public class FornaxManagerServerBootstrap extends ServerBootstrap<ManagerConfiguration> {

    public static final String APP_NAME = "fornax-manager";

    private FornaxManagerServerBootstrap(String[] bootstrapArgs) {
        super(bootstrapArgs, new ManagerConfiguration(), APP_NAME);
    }

    @Override
    protected Server newServerInstance() {
        return new FornaxManagerServer();
    }

    public static void main(String[] args) {
        new FornaxManagerServerBootstrap(args).bootstrap();
    }

}
