package zk.fornax.manager.db.mangodb;

import java.util.Collection;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import zk.fornax.common.exception.FornaxRuntimeException;
import zk.fornax.http.framework.Po;

public class CollectionOfEntityReferenceCodec<T extends Po<?>> implements Codec<Collection<T>> {

    private final Codec<T> codec;

    private final Class<Collection<T>> collectionClass;

    public CollectionOfEntityReferenceCodec(EntityReferenceCodec<T> codec, Class<Collection<T>> collectionClass) {
        this.codec = codec;
        this.collectionClass = collectionClass;
    }

    @Override
    public Collection<T> decode(BsonReader reader, DecoderContext decoderContext) {
        Collection<T> collection;
        try {
            collection = getEncoderClass().getConstructor().newInstance();
        } catch (ReflectiveOperationException exception) {
            throw new FornaxRuntimeException(exception);
        }
        reader.readStartArray();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            if (reader.getCurrentBsonType() == BsonType.NULL) {
                collection.add(null);
                reader.readNull();
            } else {
                collection.add(codec.decode(reader, decoderContext));
            }
        }
        reader.readEndArray();
        return collection;
    }

    @Override
    public void encode(BsonWriter writer, Collection<T> collection, EncoderContext encoderContext) {
        writer.writeStartArray();
        for (final T value : collection) {
            if (value == null) {
                writer.writeNull();
            } else {
                codec.encode(writer, value, encoderContext);
            }
        }
        writer.writeEndArray();
    }

    @Override
    public Class<Collection<T>> getEncoderClass() {
        return collectionClass;
    }

}
