package zk.fornax.manager;

import lombok.extern.slf4j.Slf4j;

import zk.fornax.http.core.Server;

@Slf4j
public class FornaxManagerServer implements Server {

    @Override
    public void startup() {
        log.info("FornaxManagerServer startup");
    }

    @Override
    public void shutdown() {
        log.info("FornaxManagerServer shutdown");
    }

    @Override
    public void awaitShutdown() {
        log.info("FornaxManagerServer awaitShutdown");
    }

}
