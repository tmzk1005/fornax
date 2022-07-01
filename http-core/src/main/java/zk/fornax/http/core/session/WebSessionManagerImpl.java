package zk.fornax.http.core.session;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import io.netty.handler.codec.http.cookie.Cookie;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import zk.fornax.http.core.exchange.WebExchange;

public class WebSessionManagerImpl implements WebSessionManager {

    private final WebSessionStore sessionStore = new InMemoryWebSessionStore();

    @Override
    public Mono<WebSession> getSession(WebExchange exchange) {
        return retrieveSession(exchange).switchIfEmpty(sessionStore.createWebSession());
    }

    private Mono<WebSession> retrieveSession(WebExchange exchange) {
        return Flux.fromIterable(resolveSessionIds(exchange))
            .concatMap(this.sessionStore::retrieveSession)
            .next();
    }

    private List<String> resolveSessionIds(WebExchange webExchange) {
        final Set<Cookie> cookies = webExchange.getRequest().cookies().get(WebSession.COOKIE_NAME_SESSION);
        if (Objects.isNull(cookies)) {
            return List.of();
        }
        return cookies.stream().map(Cookie::value).toList();
    }

}
