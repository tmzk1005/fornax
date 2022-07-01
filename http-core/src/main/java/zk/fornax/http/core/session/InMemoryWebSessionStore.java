package zk.fornax.http.core.session;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class InMemoryWebSessionStore implements WebSessionStore {

    private int maxSessions = 10000;

    private Clock clock = Clock.system(ZoneId.of("GMT"));

    private final Map<String, InMemoryWebSession> sessions = new ConcurrentHashMap<>();

    private final ExpiredSessionChecker expiredSessionChecker = new ExpiredSessionChecker();

    public void setMaxSessions(int maxSessions) {
        this.maxSessions = maxSessions;
    }

    public int getMaxSessions() {
        return this.maxSessions;
    }

    public void setClock(Clock clock) {
        Objects.requireNonNull(clock);
        this.clock = clock;
        removeExpiredSessions();
    }

    public Clock getClock() {
        return this.clock;
    }

    public Map<String, WebSession> getSessions() {
        return Collections.unmodifiableMap(this.sessions);
    }

    @Override
    public Mono<WebSession> createWebSession() {
        Instant now = this.clock.instant();
        this.expiredSessionChecker.checkIfNecessary(now);
        return Mono.<WebSession>fromSupplier(() -> new InMemoryWebSession(now))
            .subscribeOn(Schedulers.boundedElastic())
            .publishOn(Schedulers.parallel());
    }

    @Override
    public Mono<WebSession> retrieveSession(String id) {
        Instant now = this.clock.instant();
        this.expiredSessionChecker.checkIfNecessary(now);
        InMemoryWebSession session = this.sessions.get(id);
        if (session == null) {
            return Mono.empty();
        } else if (session.isExpired(now)) {
            this.sessions.remove(id);
            return Mono.empty();
        } else {
            session.updateLastAccessTime(now);
            return Mono.just(session);
        }
    }

    @Override
    public Mono<Void> removeSession(String id) {
        this.sessions.remove(id);
        return Mono.empty();
    }

    @Override
    public Mono<WebSession> updateLastAccessTime(WebSession session) {
        return Mono.fromSupplier(() -> {
            ((InMemoryWebSession) session).updateLastAccessTime(this.clock.instant());
            return session;
        });
    }

    public void removeExpiredSessions() {
        this.expiredSessionChecker.removeExpiredSessions(this.clock.instant());
    }

    private class InMemoryWebSession implements WebSession {

        private final AtomicReference<String> id = new AtomicReference<>(String.valueOf(UUID.randomUUID()));

        private final Map<String, Object> attributes = new ConcurrentHashMap<>();

        private final Instant creationTime;

        private volatile Instant lastAccessTime;

        private volatile Duration maxIdleTime = Duration.ofMinutes(30);

        private final AtomicReference<State> state = new AtomicReference<>(State.NEW);

        public InMemoryWebSession(Instant creationTime) {
            this.creationTime = creationTime;
            this.lastAccessTime = this.creationTime;
        }

        @Override
        public String getId() {
            return this.id.get();
        }

        @Override
        public Map<String, Object> getAttributes() {
            return this.attributes;
        }

        @Override
        public Instant getCreationTime() {
            return this.creationTime;
        }

        @Override
        public Instant getLastAccessTime() {
            return this.lastAccessTime;
        }

        @Override
        public void setMaxIdleTime(Duration maxIdleTime) {
            this.maxIdleTime = maxIdleTime;
        }

        @Override
        public Duration getMaxIdleTime() {
            return this.maxIdleTime;
        }

        @Override
        public void start() {
            this.state.compareAndSet(State.NEW, State.STARTED);
        }

        @Override
        public boolean isStarted() {
            return this.state.get().equals(State.STARTED) || !getAttributes().isEmpty();
        }

        @Override
        public Mono<Void> changeSessionId() {
            String currentId = this.id.get();
            InMemoryWebSessionStore.this.sessions.remove(currentId);
            String newId = String.valueOf(UUID.randomUUID());
            this.id.set(newId);
            InMemoryWebSessionStore.this.sessions.put(this.getId(), this);
            return Mono.empty();
        }

        @Override
        public Mono<Void> invalidate() {
            this.state.set(State.EXPIRED);
            getAttributes().clear();
            InMemoryWebSessionStore.this.sessions.remove(this.id.get());
            return Mono.empty();
        }

        @Override
        public Mono<Void> save() {
            checkMaxSessionsLimit();
            if (!getAttributes().isEmpty()) {
                this.state.compareAndSet(State.NEW, State.STARTED);
            }
            if (isStarted()) {
                InMemoryWebSessionStore.this.sessions.put(this.getId(), this);
                if (this.state.get().equals(State.EXPIRED)) {
                    InMemoryWebSessionStore.this.sessions.remove(this.getId());
                    return Mono.error(new IllegalStateException("Session was invalidated"));
                }
            }
            return Mono.empty();
        }

        private void checkMaxSessionsLimit() {
            if (sessions.size() >= maxSessions) {
                expiredSessionChecker.removeExpiredSessions(clock.instant());
                if (sessions.size() >= maxSessions) {
                    throw new IllegalStateException("Max sessions limit reached: " + sessions.size());
                }
            }
        }

        @Override
        public boolean isExpired() {
            return isExpired(clock.instant());
        }

        private boolean isExpired(Instant now) {
            if (this.state.get().equals(State.EXPIRED)) {
                return true;
            }
            if (checkExpired(now)) {
                this.state.set(State.EXPIRED);
                return true;
            }
            return false;
        }

        private boolean checkExpired(Instant currentTime) {
            return isStarted() && !this.maxIdleTime.isNegative() &&
                currentTime.minus(this.maxIdleTime).isAfter(this.lastAccessTime);
        }

        private void updateLastAccessTime(Instant currentTime) {
            this.lastAccessTime = currentTime;
        }
    }

    private class ExpiredSessionChecker {

        private static final int CHECK_PERIOD = 60 * 1000;

        private final ReentrantLock lock = new ReentrantLock();

        private Instant checkTime = clock.instant().plus(CHECK_PERIOD, ChronoUnit.MILLIS);

        public void checkIfNecessary(Instant now) {
            if (this.checkTime.isBefore(now)) {
                removeExpiredSessions(now);
            }
        }

        public void removeExpiredSessions(Instant now) {
            if (sessions.isEmpty()) {
                return;
            }
            if (this.lock.tryLock()) {
                try {
                    Iterator<InMemoryWebSession> iterator = sessions.values().iterator();
                    while (iterator.hasNext()) {
                        InMemoryWebSession session = iterator.next();
                        if (session.isExpired(now)) {
                            iterator.remove();
                            session.invalidate();
                        }
                    }
                } finally {
                    this.checkTime = now.plus(CHECK_PERIOD, ChronoUnit.MILLIS);
                    this.lock.unlock();
                }
            }
        }
    }

    private enum State {
        NEW, STARTED, EXPIRED
    }

}
