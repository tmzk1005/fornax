package zk.fornax.http.core;

import java.nio.file.Path;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Option;

public class ServerConfiguration implements Runnable {

    protected ServerConfiguration() {
    }

    @Getter
    protected boolean inited = false;

    @Getter
    @Setter
    protected Path fornaxHome;

    @Setter
    @Getter
    @Option(names = "--log.path", description = "The directory logs will be write to")
    protected String logPath = "logs";

    @Setter
    @Getter
    @Option(names = "--log.level", description = "The log level configuration for log4j2")
    protected LogLevel logLevel = LogLevel.INFO;

    @Setter
    @Getter
    @Option(names = "--conf", description = "The server configuation file")
    protected String confFile;

    @Getter
    @Option(names = "--server.host", description = "The host http server will bind to")
    protected String serverHost = "0.0.0.0";

    @Getter
    @Option(names = "--server.port", description = "The port http server will listen to")
    protected int serverPort = 8000;

    @Override
    public void run() {
        inited = true;
    }

    public enum LogLevel {
        OFF, FATAL, ERROR, WARN, INFO, DEBUG, TRACE, ALL
    }

}
