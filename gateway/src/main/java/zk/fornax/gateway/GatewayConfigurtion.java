package zk.fornax.gateway;

import lombok.Getter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import zk.fornax.http.core.ServerConfiguration;

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
public class GatewayConfigurtion extends ServerConfiguration {

    private static final String DEFAULT_CONF_FILE = "conf/fornax-gateway.properties";

    private static final int DEFAULT_PORT = 8000;

    public GatewayConfigurtion() {
        this.confFile = DEFAULT_CONF_FILE;
        this.serverPort = DEFAULT_PORT;
    }

    @Getter
    @Option(names = "--api.localtion.file", description = "The json file define http apis")
    private String apiJsonFile = "conf/fornax-gateway-httpapis.json";

}
