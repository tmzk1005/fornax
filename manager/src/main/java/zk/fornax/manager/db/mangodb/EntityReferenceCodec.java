package zk.fornax.manager.db.mangodb;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;

import zk.fornax.common.exception.FornaxRuntimeException;
import zk.fornax.http.framework.Po;

public class EntityReferenceCodec<T extends Po<?>> implements Codec<T> {

    private final Class<T> entityClass;

    public EntityReferenceCodec(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public T decode(BsonReader reader, DecoderContext decoderContext) {
        String refId = reader.readObjectId().toString();
        T instance;
        try {
            instance = getEncoderClass().getConstructor().newInstance();
        } catch (ReflectiveOperationException exception) {
            throw new FornaxRuntimeException(exception);
        }
        instance.setId(refId);
        return instance;
    }

    @Override
    public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
        writer.writeObjectId(new ObjectId(value.getId()));
    }

    @Override
    public Class<T> getEncoderClass() {
        return entityClass;
    }

}
