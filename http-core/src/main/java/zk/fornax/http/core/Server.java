package zk.fornax.http.core;

public interface Server {

    void startup();

    void shutdown();

    void awaitShutdown();

}
