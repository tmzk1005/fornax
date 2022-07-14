package zk.fornax.http.framework.validate;

import java.util.List;

public interface Validatable {

    default List<String> validate() {
        return List.of();
    }

}
