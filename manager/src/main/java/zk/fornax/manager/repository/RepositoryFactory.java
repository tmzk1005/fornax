package zk.fornax.manager.repository;

import java.util.HashMap;
import java.util.Map;

import com.mongodb.reactivestreams.client.MongoClient;

import zk.fornax.manager.FornaxManagerServerBootstrap;
import zk.fornax.manager.bean.po.ApiGroupEntity;
import zk.fornax.manager.bean.po.User;
import zk.fornax.manager.db.mangodb.MongodbHelper;

public class RepositoryFactory {

    private static final Map<Class<?>, Object> REPOSITORY_MAP = new HashMap<>();

    static {
        MongoClient mongoClient = MongodbHelper.getMongoClient();
        String databaseName = FornaxManagerServerBootstrap.managerConfiguration.getMongoDbName();
        REPOSITORY_MAP.put(UserRepository.class, new UserRepository(mongoClient, databaseName, User.class));
        REPOSITORY_MAP.put(ApiGroupRepository.class, new ApiGroupRepository(mongoClient, databaseName, ApiGroupEntity.class));
    }

    private RepositoryFactory() {
    }

    @SuppressWarnings("unchecked")
    public static <R> R get(Class<R> respositoryClass) {
        return (R) REPOSITORY_MAP.get(respositoryClass);
    }

}
