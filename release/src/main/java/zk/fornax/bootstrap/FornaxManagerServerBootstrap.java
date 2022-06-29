package zk.fornax.bootstrap;

import zk.fornax.common.log4j2.Log4j2Configurator;
import zk.fornax.manager.FornaxManagerServer;

public class FornaxManagerServerBootstrap {

    public static void main(String[] args) {
        Log4j2Configurator.configure("fornax-manager");
        new FornaxManagerServer().startup();
    }

}
