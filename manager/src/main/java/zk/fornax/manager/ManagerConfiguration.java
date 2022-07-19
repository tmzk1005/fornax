package zk.fornax.manager;

import picocli.CommandLine.Command;

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

}
