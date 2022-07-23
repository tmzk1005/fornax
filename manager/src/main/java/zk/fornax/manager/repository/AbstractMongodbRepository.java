package zk.fornax.manager.repository;

import java.time.Instant;
import java.util.List;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import zk.fornax.manager.bean.Page;
import zk.fornax.manager.bean.PageData;
import zk.fornax.manager.bean.Role;
import zk.fornax.manager.bean.po.AuditableEntity;
import zk.fornax.manager.bean.po.User;
import zk.fornax.manager.db.mangodb.MongoFilter;
import zk.fornax.manager.db.mangodb.MongodbHelper;
import zk.fornax.manager.db.mangodb.MongodbOperations;
import zk.fornax.manager.security.ContextHelper;

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
        return ContextHelper.getCurrentUser().flatMap(user -> {
            entity.setCreatedBy(user);
            entity.setLastModifiedBy(user);
            return MongodbOperations.insert(mongoCollection, entity);
        }).switchIfEmpty(MongodbOperations.insert(mongoCollection, entity));
    }

    public Mono<E> save(E entity) {
        Instant now = Instant.now();
        entity.setLastModifiedDate(now);
        return ContextHelper.getCurrentUser().flatMap(user -> {
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
        return ContextHelper.getCurrentUser().flatMap(user -> {
            MongoFilter filter = MongoFilter.byId(id);
            if (user.getRole().equals(Role.NORMAL_USER)) {
                filter = filter.andByCreatorId(user.getId());
            }
            return findOne(filter);
        }).switchIfEmpty(Mono.empty());
    }

    private Mono<MongoFilter> ownerFilter(final MongoFilter mongoFilter) {
        return ContextHelper.getCurrentUser().map(user -> {
            if (user.getRole().equals(Role.NORMAL_USER)) {
                mongoFilter.andByCreatorId(user.getId());
            }
            return mongoFilter;
        });
    }

    public Flux<E> findAndFilterByOwner(MongoFilter mongoFilter) {
        return ownerFilter(mongoFilter).flatMapMany(this::find).switchIfEmpty(Flux.empty());
    }

    public Mono<PageData<E>> pageFindAndFilterByOwner(MongoFilter mongoFilter) {
        Mono<MongoFilter> ownerFilter = ownerFilter(mongoFilter);
        Mono<List<E>> dataMono = ownerFilter.flatMapMany(this::find).switchIfEmpty(Flux.empty()).collectList();
        Mono<Long> countMono = ownerFilter.flatMap(this::count);
        Page page = mongoFilter.getPage();
        return Mono.zip(dataMono, countMono).map(tuple2 -> new PageData<>(tuple2.getT1(), tuple2.getT2(), page.getPageNum(), page.getPageSize()));
    }

    public Mono<Long> count(MongoFilter mongoFilter) {
        return MongodbOperations.count(mongoCollection, mongoFilter.getFilter());
    }

    public Mono<Long> countAndFilterByOwner(MongoFilter mongoFilter) {
        return ownerFilter(mongoFilter).flatMap(this::count);
    }

    public Mono<Void> delete(MongoFilter mongoFilter) {
        return MongodbOperations.delete(mongoCollection, mongoFilter.getFilter());
    }

}
