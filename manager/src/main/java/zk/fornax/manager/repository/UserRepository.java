package zk.fornax.manager.repository;

import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.MongoClient;
import reactor.core.publisher.Mono;

import zk.fornax.manager.bean.po.User;
import zk.fornax.manager.db.mangodb.MongoFilter;

public class UserRepository extends AbstractMongodbRepository<User> {

    public UserRepository(MongoClient mongoClient, String databaseName, Class<User> entityClass) {
        super(mongoClient, databaseName, entityClass);
    }

    public Mono<User> findOneByUsername(String username) {
        return findOne(MongoFilter.filter(Filters.eq("username", username)));
    }

}
