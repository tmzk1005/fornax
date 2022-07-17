package zk.fornax.gateway;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

import org.apache.logging.log4j.Level;
import picocli.CommandLine;

import zk.fornax.common.exception.FornaxRuntimeException;
import zk.fornax.common.log4j2.Log4j2Configurator;
import zk.fornax.gateway.GatewayConfigurtion.LogLevel;

public class FornaxGatewayServerBootstrap {

    public static final String APP_NAME = "fornax-gateway";

    private final String[] bootstrapArgs;

    private String fornaxHome;

    private final Properties confProperties = new Properties();

    private final GatewayConfigurtion gatewayConfigurtion = new GatewayConfigurtion();

    private FornaxGatewayServerBootstrap(String[] args) {
        this.bootstrapArgs = args;
    }

    private void main() {
        checkFornaxHome();
        preParseArgs();
        parseConfFile();
        parseBootstrapArgs();
        setupLogger();
        start();
    }

    private void checkFornaxHome() {
        fornaxHome = System.getenv(GatewayConfigurtion.FORNAX_HOME);
        if (Objects.isNull(fornaxHome)) {
            throw noStackTrace(new FornaxRuntimeException("System environment varialbe FORNAX_HOME required!"));
        }
        Path fornaxHomePath = Paths.get(fornaxHome);
        if (Files.notExists(fornaxHomePath) || !Files.isDirectory(fornaxHomePath)) {
            throw noStackTrace(new FornaxRuntimeException("System environment varialbe FORNAX_HOME " + fornaxHome + " is not an exist directory."));
        }
        GatewayConfigurtion.fornaxHome = Paths.get(fornaxHome);
    }

    private void preParseArgs() {
        for (String arg : bootstrapArgs) {
            if (arg.startsWith("--log.path=")) {
                gatewayConfigurtion.setLogPath(arg.substring(11));
            } else if (arg.startsWith("--log.level=")) {
                String logLevelStr = arg.substring(12);
                gatewayConfigurtion.setLogLevel(parseLogLevelStr(logLevelStr));
            } else if (arg.startsWith("--conf=")) {
                gatewayConfigurtion.setConfFile(arg.substring(7));
            }
        }
    }

    private void setupLogger() {
        Log4j2Configurator.configure(Level.getLevel(gatewayConfigurtion.getLogLevel().name()), APP_NAME, gatewayConfigurtion.getLogPath());
    }

    private void parseConfFile() {
        String confFile = gatewayConfigurtion.getConfFile();
        Path confFilePath = Paths.get(confFile);
        if (!confFilePath.isAbsolute()) {
            confFilePath = GatewayConfigurtion.getFornaxHome().resolve(confFilePath);
        }
        try {
            confProperties.load(Files.newInputStream(confFilePath));
        } catch (IOException ioException) {
            throw new FornaxRuntimeException("Failed to load configuations from properties file " + confFilePath, ioException);
        }
    }

    private void parseBootstrapArgs() {
        CommandLine commandLine = new CommandLine(gatewayConfigurtion);
        commandLine.setUsageHelpLongOptionsMaxWidth(40);
        commandLine.setDefaultValueProvider(new CommandLine.PropertiesDefaultProvider(confProperties));
        int exitCode = commandLine.execute(bootstrapArgs);
        if (exitCode != 0 || !gatewayConfigurtion.methodRunExecuted) {
            System.exit(exitCode);
        }
    }

    private void start() {
        final FornaxGatewayServer fornaxGatewayServer = new FornaxGatewayServer(gatewayConfigurtion);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> fornaxGatewayServer.shutdown()));
        fornaxGatewayServer.startup();
    }

    private LogLevel parseLogLevelStr(String logLevelStr) {
        LogLevel logLevel;
        try {
            logLevel = LogLevel.valueOf(logLevelStr);
        } catch (IllegalArgumentException ile) {
            logLevel = gatewayConfigurtion.getLogLevel();
        }
        return logLevel;
    }

    public static void main(String[] args) {
        new FornaxGatewayServerBootstrap(args).main();
    }

    private static RuntimeException noStackTrace(RuntimeException exception) {
        exception.setStackTrace(new StackTraceElement[0]);
        return exception;
    }

}
