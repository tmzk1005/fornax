package zk.fornax.gateway.locator;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import zk.fornax.common.exception.FornaxRuntimeException;
import zk.fornax.common.httpapi.ApiStatus;
import zk.fornax.common.httpapi.HttpApi;
import zk.fornax.common.rest.RestShuck;
import zk.fornax.common.utils.JsonUtil;
import zk.fornax.http.core.AntPathMatcher;
import zk.fornax.http.core.HttpApiLocator;

@Log4j2
public class GatewayHttpApiLocator implements HttpApiLocator {

    private static final String PATTERN_PATH_KEY = "$nonConstantPath";

    private final ScheduledExecutorService scheduledExecutorService;

    private final Map<String, HttpApis> path2HttpApis = new ConcurrentHashMap<>();

    private final Map<String, Flux<HttpApi>> cache = new ConcurrentHashMap<>();

    private final ApiSynchronizer apiSynchronizer = new ApiSynchronizer();

    private final String managerServerAddrees;

    public GatewayHttpApiLocator(String managerServerAddrees) {
        this.managerServerAddrees = managerServerAddrees;
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public Flux<HttpApi> getHttpApis(String path) {
        return Flux.empty();
    }

    @Override
    public void startup() {
        if (scheduledExecutorService.isShutdown() || scheduledExecutorService.isTerminated()) {
            log.error("Can not start GatewayHttpApiLocator because it's already started.");
            return;
        }
        log.info("Start GatewayHttpApiLocator.");
    }

    @Override
    public void shutdown() {
        if (!scheduledExecutorService.isShutdown()) {
            log.info("Stop GatewayHttpApiLocator.");
            scheduledExecutorService.shutdownNow();
        }
    }

    public void fetchChangedApis() {
        try {
            apiSynchronizer.fetchChangedApis();
        } catch (Exception exception) {
            log.error("Exception happened while fetch changed apis from FornaxMangerServer.", exception);
        }
    }

    private class ApiSynchronizer {

        public static final String MANAGER_FETCH_API_URL_PREFIX = "/fornax/Api/HttpApi?timeMillis=";

        private long latestTimestamp = 0L;

        private final HttpClient httpClient;

        public ApiSynchronizer() {
            this.httpClient = HttpClient.create();
        }

        public synchronized void fetchChangedApis() {
            String uri = managerServerAddrees + MANAGER_FETCH_API_URL_PREFIX + latestTimestamp;
            log.info("Start to fetch changed apis by do request: GET {}", uri);
            httpClient.get().uri(uri)
                .responseContent()
                .asInputStream()
                .onErrorResume(throwable -> {
                    log.error("Failed to fetch changed apis.", throwable);
                    return Mono.empty();
                })
                .subscribe(inputStream -> {
                    try {
                        HttpApiListRestShuck httpApiListRestShuck = JsonUtil.readValue(inputStream, HttpApiListRestShuck.class);
                        int[] changedCount = handleFetchData(httpApiListRestShuck.getData());
                        log.info("Fetch changed apis succeed. {} added (or updated), {} removed.", changedCount[0], changedCount[1]);
                    } catch (IOException ioException) {
                        throw new FornaxRuntimeException("Failed to deserialize response content to HttpApiListRestShuck.", ioException);
                    }
                });
        }

        private int[] handleFetchData(List<HttpApi> httpApiList) {
            int[] apiCount = new int[2];
            long maxTime = 0L;
            final AntPathMatcher antPathMatcher = AntPathMatcher.getDefaultInstance();
            for (HttpApi httpApi : httpApiList) {
                final String path = httpApi.getPath();
                String key;
                if (antPathMatcher.isPattern(path)) {
                    key = PATTERN_PATH_KEY;
                } else {
                    key = Paths.get(path).normalize().toString();
                }
                if (httpApi.getApiStatus().equals(ApiStatus.ONLINE)) {
                    addHttpApi(key, httpApi);
                    apiCount[0]++;
                } else {
                    apiCount[1] += removeHttpApi(key, httpApi);
                }
                maxTime = Math.max(maxTime, httpApi.getLastModifiedTimestamp());
            }
            latestTimestamp = Math.max(latestTimestamp, maxTime);
            updateCache();
            return apiCount;
        }

        private void updateCache() {
            final HttpApis patternKeyHttpApis = path2HttpApis.getOrDefault(PATTERN_PATH_KEY, new HttpApis());
            boolean pattenKeyChanged = patternKeyHttpApis.changed;
            for (Map.Entry<String, HttpApis> entry : path2HttpApis.entrySet()) {
                String path = entry.getKey();
                if (!PATTERN_PATH_KEY.equals(path)) {
                    final HttpApis httpApis = entry.getValue();
                    if (pattenKeyChanged || httpApis.changed) {
                        cache.put(path, Flux.concat(httpApis.getHttpApiFlux(), patternKeyHttpApis.getHttpApiFlux()));
                    }
                }
            }
        }

        public void addHttpApi(String key, HttpApi httpApi) {
            path2HttpApis.computeIfAbsent(key, s -> new HttpApis());
            log.info("Add Api: {}", httpApi);
            path2HttpApis.get(key).addHttpApi(httpApi);
        }

        public int removeHttpApi(String key, HttpApi httpApi) {
            if (!path2HttpApis.containsKey(key)) {
                return 0;
            }
            log.info("Remove Api: {}", httpApi);
            path2HttpApis.get(key).removeHttpApi(httpApi);
            return 1;
        }

    }

    @EqualsAndHashCode(callSuper = true)
    private static class HttpApis extends ConcurrentHashMap<String, HttpApi> {

        private transient volatile boolean changed = false;

        private transient Flux<HttpApi> httpApiFlux = Flux.empty();

        public void addHttpApi(HttpApi httpApi) {
            put(httpApi.getId(), httpApi);
            changed = true;
        }

        public void removeHttpApi(HttpApi httpApi) {
            remove(httpApi.getId());
            changed = true;
        }

        public Flux<HttpApi> getHttpApiFlux() {
            if (changed) {
                httpApiFlux = Flux.fromIterable(this.values());
                changed = false;
            }
            return httpApiFlux;
        }

    }

    private static class HttpApiListRestShuck extends RestShuck<List<HttpApi>> {
    }

}
