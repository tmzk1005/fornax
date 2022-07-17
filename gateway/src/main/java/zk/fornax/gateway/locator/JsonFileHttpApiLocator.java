package zk.fornax.gateway.locator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;

import zk.fornax.common.httpapi.HttpApi;
import zk.fornax.gateway.GatewayConfigurtion;
import zk.fornax.http.core.HttpApiLocator;

@Log4j2
public class JsonFileHttpApiLocator implements HttpApiLocator {

    @Getter
    private final String jsonPath;

    private final Flux<HttpApi> httpApis;

    public JsonFileHttpApiLocator(String jsonPath) {
        this.jsonPath = jsonPath;
        HttpApi[] httpApiArr = loadHttpApisFromFile(jsonPath);
        this.httpApis = Flux.fromArray(httpApiArr);
    }

    @Override
    public Flux<HttpApi> getHttpApis(String path) {
        return httpApis;
    }

    private static HttpApi[] loadHttpApisFromFile(String path) {
        Path jsonFilePath = Paths.get(path);
        if (!jsonFilePath.isAbsolute()) {
            jsonFilePath = GatewayConfigurtion.getFornaxHome().resolve(jsonFilePath);
        }
        try (InputStream inputStream = Files.newInputStream(jsonFilePath);) {
            return new ObjectMapper().readValue(inputStream, HttpApi[].class);
        } catch (IOException ioException) {
            log.error("Failed to load HttpApi instances from json file {}", jsonFilePath, ioException);
            return new HttpApi[0];
        }
    }

}
