package zk.fornax.manager.db.mangodb;

import com.mongodb.MongoException;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.reactivestreams.Publisher;

public interface ReactiveCollectionCallback<T, E> {

    Publisher<E> doInCollection(MongoCollection<T> collection) throws MongoException;

}
