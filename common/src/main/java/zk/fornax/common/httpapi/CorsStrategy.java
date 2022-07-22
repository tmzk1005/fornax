package zk.fornax.common.httpapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CorsStrategy {

    private String id;

    private List<String> allowedOrigins = new ArrayList<>(List.of("*"));

    private List<String> allowedOriginPatterns = new ArrayList<>(List.of("*"));

    private List<HttpMethod> allowedMethods = Arrays.asList(
        HttpMethod.GET,
        HttpMethod.HEAD,
        HttpMethod.POST,
        HttpMethod.PUT,
        HttpMethod.PATCH,
        HttpMethod.DELETE,
        HttpMethod.OPTIONS,
        HttpMethod.TRACE
    );

    private List<String> allowedHeaders = new ArrayList<>(List.of("*"));

    private List<String> exposedHeaders = new ArrayList<>(List.of("*"));

    private boolean allowCredentials = false;

    private Long maxAge = 0L;

}
