package zk.fornax.manager;

import lombok.Getter;

import zk.fornax.http.core.Server;
import zk.fornax.http.core.ServerBootstrap;

public class FornaxManagerServerBootstrap extends ServerBootstrap<ManagerConfiguration> {

    public static final String APP_NAME = "fornax-manager";

    @Getter
    public static ManagerConfiguration managerConfiguration;

    private FornaxManagerServerBootstrap(String[] bootstrapArgs) {
        super(bootstrapArgs, FornaxManagerServerBootstrap.managerConfiguration, APP_NAME);
    }

    @Override
    protected Server newServerInstance() {
        return new FornaxManagerServer(managerConfiguration);
    }

    public static void main(String[] args) {
        managerConfiguration = new ManagerConfiguration();
        new FornaxManagerServerBootstrap(args).bootstrap();
    }

}
