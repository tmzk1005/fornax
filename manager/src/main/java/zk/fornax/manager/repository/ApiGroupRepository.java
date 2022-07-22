package zk.fornax.manager.repository;

import com.mongodb.reactivestreams.client.MongoClient;

import zk.fornax.manager.bean.po.ApiGroupEntity;

public class ApiGroupRepository extends AbstractMongodbRepository<ApiGroupEntity> {

    public ApiGroupRepository(MongoClient mongoClient, String databaseName, Class<ApiGroupEntity> entityClass) {
        super(mongoClient, databaseName, entityClass);
    }

}
