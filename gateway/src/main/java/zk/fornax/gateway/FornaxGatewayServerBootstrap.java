package zk.fornax.gateway;

import org.apache.logging.log4j.Level;
import picocli.CommandLine;

import zk.fornax.common.log4j2.Log4j2Configurator;

public class FornaxGatewayServerBootstrap {

    public static void main(String[] args) {
        final GatewayConfigurtion gatewayConfigurtion = new GatewayConfigurtion();
        int exitCode = new CommandLine(gatewayConfigurtion).execute(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
        Log4j2Configurator.configure(Level.getLevel(gatewayConfigurtion.getLogLevel().name()), "fornax-gateway", gatewayConfigurtion.getLogPath());
        final FornaxGatewayServer fornaxGatewayServer = new FornaxGatewayServer(gatewayConfigurtion);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> fornaxGatewayServer.shutdown()));
        fornaxGatewayServer.startup();
    }

}
