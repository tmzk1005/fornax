package zk.fornax.http.core.session;

import reactor.core.publisher.Mono;

public interface WebSessionStore {

    Mono<WebSession> createWebSession();

    Mono<WebSession> retrieveSession(String sessionId);

    Mono<Void> removeSession(String sessionId);

    Mono<WebSession> updateLastAccessTime(WebSession webSession);

}
