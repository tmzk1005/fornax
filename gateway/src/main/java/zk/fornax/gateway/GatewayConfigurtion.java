package zk.fornax.gateway;

import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Log4j2
@Command(name = "fornax-gateway", version = "1.0", mixinStandardHelpOptions = true, description = "A http api gateway server.")
public class GatewayConfigurtion implements Runnable {

    private final AtomicBoolean init = new AtomicBoolean(false);

    @Getter
    @Option(names = "--log.path", description = "The directory logs will be write to")
    private String logPath = "logs";

    @Getter
    @Option(names = "--log.level", description = "The log level configuration for log4j2")
    private LogLevel logLevel = LogLevel.INFO;

    @Getter
    @Option(names = "--conf", description = "The gateway server configuation file")
    private String confFile;

    @Getter
    @Option(names = "--server.host", description = "The host gateway server will bind to")
    private String serverHost = "0.0.0.0";

    @Getter
    @Option(names = "--server.port", description = "The port gateway server will listen to")
    private int serverPort = 8000;

    @Getter
    @Option(names = "--api.localtion.file", description = "The json file define http apis")
    private String apiJsonFile;

    @Override
    public void run() {
        if (init.compareAndSet(false, true)) {
            initAfterCommandLineParsed();
        }
    }

    private void initAfterCommandLineParsed() {
        log.info("Command line arguments parsed succceed.");
    }

    enum LogLevel {
        OFF, FATAL, ERROR, WARN, INFO, DEBUG, TRACE, ALL
    }

}
