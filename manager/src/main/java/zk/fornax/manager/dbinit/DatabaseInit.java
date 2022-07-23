package zk.fornax.manager.dbinit;

import java.util.Objects;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.bson.Document;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import zk.fornax.http.framework.security.Pbkdf2PasswordEncoder;
import zk.fornax.manager.FornaxManagerServerBootstrap;
import zk.fornax.manager.bean.Role;
import zk.fornax.manager.bean.po.ApiEntity;
import zk.fornax.manager.bean.po.ApiGroupEntity;
import zk.fornax.manager.bean.po.AppEntity;
import zk.fornax.manager.bean.po.User;
import zk.fornax.manager.db.mangodb.Index;
import zk.fornax.manager.db.mangodb.MongoFilter;
import zk.fornax.manager.db.mangodb.MongodbHelper;
import zk.fornax.manager.repository.RepositoryFactory;
import zk.fornax.manager.repository.UserRepository;

public class DatabaseInit {

    private DatabaseInit() {
    }

    @SuppressWarnings("PMD.CloseResource")
    public static Mono<Void> init() {
        MongoClient mongoClient = MongodbHelper.getMongoClient();
        String mongoDbName = FornaxManagerServerBootstrap.managerConfiguration.getMongoDbName();
        MongoDatabase database = mongoClient.getDatabase(mongoDbName);
        return initCollectionForEntity(User.class, database)
            .then(initCollectionForEntity(ApiGroupEntity.class, database))
            .then(initCollectionForEntity(ApiEntity.class, database))
            .then(initCollectionForEntity(AppEntity.class, database))
            .then(initFirstSystemAdminUser());
    }

    private static Mono<Void> initCollectionForEntity(Class<?> entityClass, MongoDatabase mongoDatabase) {
        MongoCollection<Document> collection = mongoDatabase.getCollection(MongodbHelper.getCollectionName(entityClass));
        Index annotation = entityClass.getAnnotation(Index.class);
        if (Objects.nonNull(annotation)) {
            return Flux.from(collection.listIndexes()).filter(document -> document.get("name").equals(annotation.name())).count().flatMap(count -> {
                if (count == 0) {
                    return createIndex(collection, annotation.name(), annotation.unique(), annotation.def());
                }
                return Mono.empty();
            });
        }
        return Mono.empty();
    }

    private static Mono<Void> createIndex(MongoCollection<Document> collection, String name, boolean unique, String definition) {
        IndexOptions indexOptions = new IndexOptions().name(name).unique(unique);
        return Mono.from(collection.createIndex(Document.parse(definition), indexOptions)).then();
    }

    private static Mono<Void> initFirstSystemAdminUser() {
        UserRepository userRepository = RepositoryFactory.get(UserRepository.class);
        String initUserName = "admin";
        String initUserPassword = "admin@fornax";
        return userRepository.findOne(MongoFilter.filter(Filters.eq("username", initUserName)))
            .switchIfEmpty(Mono.defer(() -> {
                User user = new User();
                user.setUsername(initUserName);
                user.setNickname("Admin");
                user.setPassword(Pbkdf2PasswordEncoder.getDefaultInstance().encode(initUserPassword));
                user.setEnabled(true);
                user.setRole(Role.SYSTEM_ADMIN);
                return userRepository.insert(user);
            }))
            .then();
    }

}
