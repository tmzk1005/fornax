package zk.fornax.gateway.locator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;

import zk.fornax.common.httpapi.HttpApi;
import zk.fornax.http.core.HttpApiLocator;

@Log4j2
public class JsonFileHttpApiLocator implements HttpApiLocator {

    @Getter
    private final Path jsonPath;

    private final Flux<HttpApi> httpApis;

    public JsonFileHttpApiLocator(Path jsonPath) {
        this.jsonPath = jsonPath;
        HttpApi[] httpApiArr = loadHttpApisFromFile(jsonPath);
        this.httpApis = Flux.fromArray(httpApiArr);
    }

    @Override
    public Flux<HttpApi> getHttpApis(String path) {
        return httpApis;
    }

    private static HttpApi[] loadHttpApisFromFile(Path path) {
        try (InputStream inputStream = Files.newInputStream(path);) {
            HttpApi[] httpApis = new ObjectMapper().readValue(inputStream, HttpApi[].class);
            log.info("Loaded {} apis from file {}", httpApis.length, path);
            return httpApis;
        } catch (IOException ioException) {
            log.error("Failed to load HttpApi instances from json file {}", path, ioException);
            return new HttpApi[0];
        }
    }

}
