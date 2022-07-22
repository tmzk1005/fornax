package zk.fornax.manager.repository;

import com.mongodb.reactivestreams.client.MongoClient;

import zk.fornax.manager.bean.po.ApiEntity;

public class ApiRepository extends AbstractMongodbRepository<ApiEntity> {

    public ApiRepository(MongoClient mongoClient, String databaseName, Class<ApiEntity> entityClass) {
        super(mongoClient, databaseName, entityClass);
    }
    
}
