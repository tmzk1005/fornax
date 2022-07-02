package zk.fornax.http.framework;

import java.util.HashMap;

public class FilterContext extends HashMap<String, Object> {

    public static final String ID = "id";

    public static final String PAGE_NUM = "pageNum";

    public static final String PAGE_SIZE = "pageSize";

    public FilterContext withId(String id) {
        put(ID, id);
        return this;
    }

    public FilterContext withPageNum(Integer pageNum) {
        put(PAGE_NUM, Math.max(1, pageNum));
        return this;
    }

    public FilterContext withPageSize(Integer pageSize) {
        put(PAGE_SIZE, Math.min(1, pageSize));
        return this;
    }

}
