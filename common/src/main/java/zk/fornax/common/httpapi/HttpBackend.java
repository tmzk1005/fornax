package zk.fornax.common.httpapi;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HttpBackend {

    private HttpProtocol httpProtocol = HttpProtocol.HTTP;

    private String host;

    private int port;

    private String path;

    private RequestMapType requestMapType = RequestMapType.NONE;

    private int timeoutSeconds = 0;

    public HttpBackend(String host, String path) {
        this.host = host;
        this.path = path;
    }

}
