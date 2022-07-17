package zk.fornax.gateway;

import java.nio.file.Path;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

// @formatter:off
@Command(
    name = "fornax-gateway",
    version = "1.0",
    mixinStandardHelpOptions = true,
    description = "A http api gateway server.",
    usageHelpAutoWidth = true,
    showDefaultValues = true
)
// @formatter:on
public class GatewayConfigurtion implements Runnable {

    public static final String FORNAX_HOME = "FORNAX_HOME";

    @Getter
    static Path fornaxHome;

    boolean methodRunExecuted = false;

    @Setter
    @Getter
    @Option(names = "--log.path", description = "The directory logs will be write to")
    private String logPath = "logs";

    @Setter
    @Getter
    @Option(names = "--log.level", description = "The log level configuration for log4j2")
    private LogLevel logLevel = LogLevel.INFO;

    @Setter
    @Getter
    @Option(names = "--conf", description = "The gateway server configuation file")
    private String confFile = "conf/fornax-gateway.properties";

    @Getter
    @Option(names = "--server.host", description = "The host gateway server will bind to")
    private String serverHost = "0.0.0.0";

    @Getter
    @Option(names = "--server.port", description = "The port gateway server will listen to")
    private int serverPort = 8000;

    @Getter
    @Option(names = "--api.localtion.file", description = "The json file define http apis")
    private String apiJsonFile = "conf/fornax-gateway-httpapis.json";

    @Override
    public void run() {
        methodRunExecuted = true;
    }

    enum LogLevel {
        OFF, FATAL, ERROR, WARN, INFO, DEBUG, TRACE, ALL
    }

}
