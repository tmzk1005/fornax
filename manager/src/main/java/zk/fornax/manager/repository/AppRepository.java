package zk.fornax.manager.repository;

import com.mongodb.reactivestreams.client.MongoClient;

import zk.fornax.manager.bean.po.AppEntity;

public class AppRepository extends AbstractMongodbRepository<AppEntity> {

    public AppRepository(MongoClient mongoClient, String databaseName, Class<AppEntity> entityClass) {
        super(mongoClient, databaseName, entityClass);
    }

}
