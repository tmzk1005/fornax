package zk.fornax.manager.db.mangodb;

import java.util.Objects;

import com.mongodb.MongoException;
import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.FindPublisher;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.conversions.Bson;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import zk.fornax.http.framework.Po;
import zk.fornax.manager.bean.Page;

public class MongodbOperations {

    private MongodbOperations() {
    }

    public static <T, E> Mono<E> createMono(MongoCollection<T> collection, ReactiveCollectionCallback<T, E> collectionCallback) {
        return Mono.from(collectionCallback.doInCollection(collection));
    }

    public static <T, E> Flux<E> createFlux(MongoCollection<T> collection, ReactiveCollectionCallback<T, E> collectionCallback) {
        return Flux.from(collectionCallback.doInCollection(collection));
    }

    public static <T> FindPublisher<T> filterSortPage(MongoCollection<T> collection, MongoFilter mongoFilter) {
        final FindPublisher<T> findPublisher = collection.find(mongoFilter.getFilter());
        if (mongoFilter.hasSort()) {
            findPublisher.sort(mongoFilter.getSort());
        }
        if (mongoFilter.hasPage()) {
            final Page page = mongoFilter.getPage();
            findPublisher.skip(page.getOffset()).limit(page.getPageSize());
        }
        return findPublisher;
    }

    public static <T extends Po<?>> Mono<T> findOne(MongoCollection<T> mongoCollection, MongoFilter mongoFilter) {
        return createMono(mongoCollection, new FindOneCallback<>(mongoFilter));
    }

    public static <T extends Po<?>> Flux<T> find(MongoCollection<T> mongoCollection, MongoFilter mongoFilter) {
        return createFlux(mongoCollection, new FindMultiCallback<>(mongoFilter));
    }

    public static <T extends Po<?>> Mono<T> insert(MongoCollection<T> mongoCollection, T entity) {
        if (Objects.nonNull(entity.getId())) {
            throw new IllegalArgumentException("Entity to insert to mongodb should not has an id already");
        }
        return createMono(mongoCollection, new InsertOneCallback<>(entity));
    }

    public static <T extends Po<?>> Mono<T> save(MongoCollection<T> mongoCollection, T entity) {
        if (Objects.isNull(entity.getId())) {
            throw new IllegalArgumentException("Entity to save(update) to mongodb should has an id.");
        }
        return createMono(mongoCollection, new UpdateOneCallback<>(entity));
    }

    public static <T extends Po<?>> Mono<Long> count(MongoCollection<T> mongoCollection, Bson filter) {
        return Mono.from(mongoCollection.countDocuments(filter));
    }

    public static <T extends Po<?>> Mono<Void> delete(MongoCollection<T> mongoCollection, Bson filter) {
        return createMono(mongoCollection, new DeleteCallback<>(filter));
    }

    public record FindOneCallback<T> (MongoFilter mongoFilter) implements ReactiveCollectionEntityCallback<T> {

        @Override
        public Mono<T> doInCollection(MongoCollection<T> collection) throws MongoException {
            final FindPublisher<T> findPublisher = filterSortPage(collection, mongoFilter);
            return Mono.from(findPublisher.first());
        }

    }

    public record FindMultiCallback<T> (MongoFilter mongoFilter) implements ReactiveCollectionEntityCallback<T> {

        @Override
        public Flux<T> doInCollection(MongoCollection<T> collection) throws MongoException {
            final FindPublisher<T> findPublisher = filterSortPage(collection, mongoFilter);
            return Flux.from(findPublisher);
        }

    }

    public record InsertOneCallback<T> (T entity) implements ReactiveCollectionEntityCallback<T> {

        @Override
        public Publisher<T> doInCollection(MongoCollection<T> collection) throws MongoException {
            return Mono.from(collection.insertOne(entity))
                .flatMap(
                    insertOneResult -> createMono(collection, new FindOneCallback<>(MongoFilter.filter(Filters.eq("_id", insertOneResult.getInsertedId()))))
                );
        }

    }

    public record UpdateOneCallback<T extends Po<?>> (T entity) implements ReactiveCollectionEntityCallback<T> {

        @Override
        public Publisher<T> doInCollection(MongoCollection<T> collection) throws MongoException {
            return Mono.from(collection.replaceOne(MongoFilter.byId(entity.getId()).getFilter(), entity))
                .flatMap(updateResult -> createMono(collection, new FindOneCallback<>(MongoFilter.byId(entity.getId()))));
        }

    }

    public record DeleteCallback<T extends Po<?>> (Bson filter) implements ReactiveCollectionCallback<T, Void> {

        @Override
        public Publisher<Void> doInCollection(MongoCollection<T> collection) throws MongoException {
            return Mono.from(collection.deleteMany(filter)).then();
        }

    }

}
