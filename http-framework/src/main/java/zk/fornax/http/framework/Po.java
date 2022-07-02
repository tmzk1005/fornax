package zk.fornax.http.framework;

import java.io.Serializable;
import java.lang.reflect.Constructor;

public interface Po<D extends Dto> extends Serializable {

    String getId();

    void setId(String id);

    <P extends Po<D>> P setFromDto(D dto);

    static <P extends Po<D>, D extends Dto> P fromDto(Class<P> poClass, D dtoInstance) {
        Constructor<P> constructor;
        try {
            constructor = poClass.getConstructor();
            P poInstance = constructor.newInstance();
            return poInstance.setFromDto(dtoInstance);
        } catch (ReflectiveOperationException reflectiveOperationException) {
            throw new IllegalArgumentException("Class " + poClass.getName() + " does not has an default constructor.", reflectiveOperationException);
        }
    }

}
