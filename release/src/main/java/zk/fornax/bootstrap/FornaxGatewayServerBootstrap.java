package zk.fornax.bootstrap;

import zk.fornax.common.log4j2.Log4j2Configurator;
import zk.fornax.gateway.FornaxGatewayServer;

public class FornaxGatewayServerBootstrap {

    public static void main(String[] args) {
        Log4j2Configurator.configure("fornax-gateway");
        new FornaxGatewayServer().startup();
    }

}
