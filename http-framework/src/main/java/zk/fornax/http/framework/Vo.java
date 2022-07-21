package zk.fornax.http.framework;

import java.io.Serializable;
import java.lang.reflect.Constructor;

public interface Vo<P extends Po<?>> extends Serializable {

    <V extends Vo<P>> V initFromPo(P poInstance);

    static <V extends Vo<P>, P extends Po<?>> V fromPo(Class<V> voClass, P poInstance) {
        Constructor<V> constructor;
        try {
            constructor = voClass.getConstructor();
            V voInstance = constructor.newInstance();
            return voInstance.initFromPo(poInstance);
        } catch (ReflectiveOperationException reflectiveOperationException) {
            throw new IllegalArgumentException("Class " + voClass.getName() + " does not has an default constructor.", reflectiveOperationException);
        }
    }

}
