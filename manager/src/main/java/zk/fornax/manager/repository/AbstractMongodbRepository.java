package zk.fornax.manager.repository;

import java.time.Instant;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import lombok.extern.log4j.Log4j2;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import zk.fornax.manager.bean.Role;
import zk.fornax.manager.bean.po.AuditableEntity;
import zk.fornax.manager.bean.po.User;
import zk.fornax.manager.db.mangodb.MongoFilter;
import zk.fornax.manager.db.mangodb.MongodbHelper;
import zk.fornax.manager.db.mangodb.MongodbOperations;
import zk.fornax.manager.security.RoleChecker;

@Log4j2
public abstract class AbstractMongodbRepository<E extends AuditableEntity<User, Instant, ?>> {

    protected final MongoClient mongoClient;

    protected final String databaseName;

    protected final Class<E> entityClass;

    protected final MongoCollection<E> mongoCollection;

    protected AbstractMongodbRepository(MongoClient mongoClient, String databaseName, Class<E> entityClass) {
        this.mongoClient = mongoClient;
        this.databaseName = databaseName;
        this.entityClass = entityClass;
        mongoCollection = mongoClient.getDatabase(databaseName).getCollection(MongodbHelper.getCollectionName(entityClass), entityClass);
    }

    public Mono<E> insert(E entity) {
        Instant now = Instant.now();
        entity.setCreatedDate(now);
        entity.setLastModifiedDate(now);
        return RoleChecker.getCurrentUser().flatMap(user -> {
            entity.setCreatedBy(user);
            entity.setLastModifiedBy(user);
            return MongodbOperations.insert(mongoCollection, entity);
        }).switchIfEmpty(MongodbOperations.insert(mongoCollection, entity));
    }

    public Mono<E> save(E entity) {
        Instant now = Instant.now();
        entity.setLastModifiedDate(now);
        return RoleChecker.getCurrentUser().flatMap(user -> {
            entity.setLastModifiedBy(user);
            return MongodbOperations.save(mongoCollection, entity);
        }).switchIfEmpty(MongodbOperations.save(mongoCollection, entity));
    }

    public Mono<E> findOne(MongoFilter mongoFilter) {
        return MongodbOperations.findOne(mongoCollection, mongoFilter);
    }

    public Flux<E> find(MongoFilter mongoFilter) {
        return MongodbOperations.find(mongoCollection, mongoFilter);
    }

    public Mono<E> findOneById(String id) {
        try {
            new ObjectId(id);
        } catch (IllegalArgumentException exception) {
            log.warn("{} is not a validated ObjectId string.", id);
            return Mono.empty();
        }
        return findOne(MongoFilter.byId(id));
    }

    public Mono<E> findOneByIdAndFilterByOwner(String id) {
        return RoleChecker.getCurrentUser().flatMap(user -> {
            MongoFilter filter = MongoFilter.byId(id);
            if (user.getRole().equals(Role.NORMAL_USER)) {
                filter = filter.andByCreatorId(user.getId());
            }
            return findOne(filter);
        }).switchIfEmpty(Mono.empty());
    }

    public Flux<E> findAndFilterByOwner(MongoFilter mongoFilter) {
        return RoleChecker.getCurrentUser().flatMapMany(user -> {
            MongoFilter filter = mongoFilter;
            if (user.getRole().equals(Role.NORMAL_USER)) {
                filter = filter.andByCreatorId(user.getId());
            }
            return find(filter);
        }).switchIfEmpty(Flux.empty());
    }

    public Mono<Long> count(Bson filter) {
        return MongodbOperations.count(mongoCollection, filter);
    }

    public Mono<Void> delete(Bson filter) {
        return MongodbOperations.delete(mongoCollection, filter);
    }

}
