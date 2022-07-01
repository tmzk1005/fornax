package zk.fornax.gateway;

import lombok.extern.slf4j.Slf4j;

import zk.fornax.http.core.Server;

@Slf4j
public class FornaxGatewayServer implements Server {

    @Override
    public void startup() {
        log.info("FornaxGatewayServer startup");
    }

    @Override
    public void shutdown() {
        log.info("FornaxGatewayServer shutdown");
    }

}
