package zk.fornax.manager.db.mangodb;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;

import org.bson.codecs.Codec;
import org.bson.codecs.pojo.ClassModelBuilder;
import org.bson.codecs.pojo.Convention;
import org.bson.codecs.pojo.PropertyModelBuilder;
import org.bson.codecs.pojo.TypeWithTypeParameters;

import zk.fornax.common.exception.FornaxRuntimeException;
import zk.fornax.http.framework.Po;

public class CustomConvention implements Convention {

    @Override
    public void apply(ClassModelBuilder<?> classModelBuilder) {
        for (final PropertyModelBuilder<?> propertyModel : classModelBuilder.getPropertyModelBuilders()) {
            if (!"id".equals(propertyModel.getName())) {
                propertyModel.propertySerialization(new CustomPropertySerialization<>());
            }
            for (PropertyModelBuilder<?> propertyModelBuilder : classModelBuilder.getPropertyModelBuilders()) {
                processPropertyAnnotations(propertyModelBuilder);
            }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void processPropertyAnnotations(final PropertyModelBuilder<?> propertyModelBuilder) {
        for (Annotation annotation : propertyModelBuilder.getReadAnnotations()) {
            if (annotation instanceof DocumentReference) {
                TypeWithTypeParameters<?> typeInfo = getTypeInfoOfPropertyModelBuilder(propertyModelBuilder);
                Class<?> type = typeInfo.getType();
                if (Collection.class.isAssignableFrom(type)) {
                    propertyModelBuilder.codec((Codec) getCollectionOfEntityReferenceCodec(typeInfo));
                } else if (Po.class.isAssignableFrom(type)) {
                    propertyModelBuilder.codec((Codec) getEntityReferenceCodec(type));
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> TypeWithTypeParameters<T> getTypeInfoOfPropertyModelBuilder(PropertyModelBuilder<T> propertyModelBuilder) {
        try {
            Field typeDataField = propertyModelBuilder.getClass().getDeclaredField("typeData");
            typeDataField.setAccessible(true);
            return (TypeWithTypeParameters<T>) typeDataField.get(propertyModelBuilder);
        } catch (ReflectiveOperationException exception) {
            throw new FornaxRuntimeException(exception);
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Po<?>> EntityReferenceCodec<T> getEntityReferenceCodec(Class<?> entityClass) {
        return new EntityReferenceCodec<>((Class<T>) entityClass);
    }

    @SuppressWarnings("unchecked")
    private <T extends Po<?>> CollectionOfEntityReferenceCodec<T> getCollectionOfEntityReferenceCodec(TypeWithTypeParameters<?> typeInfo) {
        Class<Collection<T>> collectionClass = (Class<Collection<T>>) typeInfo.getType();
        Class<T> entityClass = (Class<T>) typeInfo.getTypeParameters().get(0).getType();
        return new CollectionOfEntityReferenceCodec<>(getEntityReferenceCodec(entityClass), collectionClass);
    }

}
