package zk.fornax.http.core.session;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;

import reactor.core.publisher.Mono;

public interface WebSession {

    String COOKIE_NAME_SESSION = "SESSION";
    String ATTR_NAME_PRINCIPAL = "__principal";

    String getId();

    Map<String, Object> getAttributes();

    @SuppressWarnings("unchecked")
    default <T> T getAttribute(String name) {
        return (T) getAttributes().get(name);
    }

    default <T> T getRequiredAttribute(String name) {
        T value = getAttribute(name);
        Objects.requireNonNull(value);
        return value;
    }

    @SuppressWarnings("unchecked")
    default <T> T getAttributeOrDefault(String name, T defaultValue) {
        return (T) getAttributes().getOrDefault(name, defaultValue);
    }

    void start();

    boolean isStarted();

    Mono<Void> changeSessionId();

    Mono<Void> invalidate();

    Mono<Void> save();

    boolean isExpired();

    Instant getCreationTime();

    Instant getLastAccessTime();

    void setMaxIdleTime(Duration maxIdleTime);

    Duration getMaxIdleTime();

}
