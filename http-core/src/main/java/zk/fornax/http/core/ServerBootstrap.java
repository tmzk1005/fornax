package zk.fornax.http.core;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

import org.apache.logging.log4j.Level;
import picocli.CommandLine;

import zk.fornax.common.exception.FornaxRuntimeException;
import zk.fornax.common.log4j2.Log4j2Configurator;
import zk.fornax.http.core.ServerConfiguration.LogLevel;

public abstract class ServerBootstrap<C extends ServerConfiguration> {

    public static final String FORNAX_HOME = "FORNAX_HOME";

    protected final String[] bootstrapArgs;

    protected String fornaxHome;

    protected Path fornaxHomePath;

    protected final Properties confProperties = new Properties();

    protected final C configuration;

    protected final String appName;

    protected ServerBootstrap(String[] bootstrapArgs, C configuration, String appName) {
        this.bootstrapArgs = bootstrapArgs;
        this.configuration = configuration;
        this.appName = appName;
    }

    protected void bootstrap() {
        checkFornaxHome();
        preParseArgs();
        parseConfFile();
        parseBootstrapArgs();
        setupLogger();
        start();
    }

    private void checkFornaxHome() {
        fornaxHome = System.getenv(FORNAX_HOME);
        if (Objects.isNull(fornaxHome)) {
            throw noStackTrace(new FornaxRuntimeException("System environment varialbe FORNAX_HOME required!"));
        }
        fornaxHomePath = Paths.get(fornaxHome);
        if (Files.notExists(fornaxHomePath) || !Files.isDirectory(fornaxHomePath)) {
            throw noStackTrace(new FornaxRuntimeException("System environment varialbe FORNAX_HOME " + fornaxHome + " is not an exist directory."));
        }
        configuration.setFornaxHome(fornaxHomePath);
    }

    private void preParseArgs() {
        for (String arg : bootstrapArgs) {
            if (arg.startsWith("--log.path=")) {
                configuration.setLogPath(arg.substring(11));
            } else if (arg.startsWith("--log.level=")) {
                String logLevelStr = arg.substring(12);
                configuration.setLogLevel(parseLogLevelStr(logLevelStr));
            } else if (arg.startsWith("--conf=")) {
                configuration.setConfFile(arg.substring(7));
            }
        }
    }

    private LogLevel parseLogLevelStr(String logLevelStr) {
        LogLevel logLevel;
        try {
            logLevel = LogLevel.valueOf(logLevelStr);
        } catch (IllegalArgumentException ile) {
            logLevel = configuration.getLogLevel();
        }
        return logLevel;
    }

    private void parseConfFile() {
        String confFile = configuration.getConfFile();
        Path confFilePath = Paths.get(confFile);
        if (!confFilePath.isAbsolute()) {
            confFilePath = fornaxHomePath.resolve(confFilePath);
        }
        try (InputStream inputStream = Files.newInputStream(confFilePath)) {
            confProperties.load(inputStream);
        } catch (IOException ioException) {
            throw new FornaxRuntimeException("Failed to load configuations from properties file " + confFilePath, ioException);
        }
    }

    private void parseBootstrapArgs() {
        CommandLine commandLine = new CommandLine(configuration);
        commandLine.setUsageHelpLongOptionsMaxWidth(40);
        commandLine.setDefaultValueProvider(new CommandLine.PropertiesDefaultProvider(confProperties));
        int exitCode = commandLine.execute(bootstrapArgs);
        if (exitCode != 0 || !configuration.isInited()) {
            System.exit(exitCode);
        }
    }

    private void setupLogger() {
        Log4j2Configurator.configure(Level.getLevel(configuration.getLogLevel().name()), appName, configuration.getLogPath());
    }

    protected void start() {
        Server server = newServerInstance();
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
        server.startup();
    }

    protected abstract Server newServerInstance();

    private static RuntimeException noStackTrace(RuntimeException exception) {
        exception.setStackTrace(new StackTraceElement[0]);
        return exception;
    }

}
