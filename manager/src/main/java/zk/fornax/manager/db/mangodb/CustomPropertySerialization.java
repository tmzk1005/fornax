package zk.fornax.manager.db.mangodb;

import org.bson.codecs.pojo.PropertySerialization;

public class CustomPropertySerialization<T> implements PropertySerialization<T> {

    @Override
    public boolean shouldSerialize(T value) {
        return true;
    }

}
