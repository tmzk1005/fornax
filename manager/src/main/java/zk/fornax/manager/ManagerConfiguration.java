package zk.fornax.manager;

import lombok.Getter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import zk.fornax.http.core.ServerConfiguration;

// @formatter:off
@Command(
    name = "fornax-manager",
    version = "1.0",
    mixinStandardHelpOptions = true,
    description = "Manager server supply restFul interfaces to controller fornax gateway server.",
    usageHelpAutoWidth = true,
    showDefaultValues = true
)
// @formatter:on
public class ManagerConfiguration extends ServerConfiguration {

    private static final String DEFAULT_CONF_FILE = "conf/fornax-manager.properties";

    private static final int DEFAULT_PORT = 9000;

    public ManagerConfiguration() {
        this.confFile = DEFAULT_CONF_FILE;
        this.serverPort = DEFAULT_PORT;
    }

    @Getter
    @Option(names = "--mongodb.database", description = "Monogo db database name to store api manager information.")
    private String mongoDbName = "fornax";

}
