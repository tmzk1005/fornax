package zk.fornax.manager.db.mangodb;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Convention;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;

import zk.fornax.common.utils.ObjectUtils;

public class MongodbHelper {

    private MongodbHelper() {
    }

    private static MongoClient mongoClient;

    public static synchronized MongoClient getMongoClient() {
        if (Objects.isNull(mongoClient)) {
            mongoClient = createDefaultMongoClient();
        }
        return mongoClient;
    }

    public static String getCollectionName(Class<?> clazz) {
        Objects.requireNonNull(clazz);
        Document documentAnno = clazz.getDeclaredAnnotation(Document.class);
        if (Objects.isNull(documentAnno)) {
            throw new IllegalArgumentException(clazz.getName() + " is not an entity class.");
        }
        String collectionName = documentAnno.collection();
        if (ObjectUtils.isEmpty(collectionName)) {
            collectionName = clazz.getSimpleName();
        }
        return collectionName;
    }

    private static MongoClient createDefaultMongoClient() {
        List<Convention> conventions = new ArrayList<>(Conventions.DEFAULT_CONVENTIONS);
        conventions.add(new CustomConvention());
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).conventions(conventions).build())
        );
        MongoClientSettings settings = MongoClientSettings.builder().codecRegistry(pojoCodecRegistry).build();
        return MongoClients.create(settings);
    }

}
