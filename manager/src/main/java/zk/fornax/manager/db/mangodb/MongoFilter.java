package zk.fornax.manager.db.mangodb;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import lombok.Getter;
import lombok.Setter;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import zk.fornax.common.utils.ObjectUtils;
import zk.fornax.manager.bean.Page;
import zk.fornax.manager.bean.po.BaseAuditableEntity;

public class MongoFilter {

    @Setter
    @Getter
    private Bson filter;

    private final List<Bson> sorts = new ArrayList<>();

    @Getter
    private Page page;

    public MongoFilter(Bson filter) {
        this.filter = filter;
    }

    public static MongoFilter filter(Bson filter) {
        return new MongoFilter(filter);
    }

    public static MongoFilter empty() {
        return filter(Filters.empty());
    }

    public static MongoFilter byId(String id) {
        return filter(Filters.eq("_id", new ObjectId(id)));
    }

    public static MongoFilter byIds(Iterable<String> ids) {
        List<ObjectId> objectIds = new ArrayList<>();
        ids.forEach(id -> objectIds.add(new ObjectId(id)));
        return filter(Filters.in("_id", objectIds));
    }

    public static MongoFilter byCreatorId(String id) {
        return filter(Filters.eq(BaseAuditableEntity.CREATED_BY, new ObjectId(id)));
    }

    public MongoFilter sort(Bson sort) {
        this.sorts.add(sort);
        return this;
    }

    public MongoFilter ascending(final String... fieldNames) {
        this.sorts.add(Sorts.ascending(fieldNames));
        return this;
    }

    public MongoFilter descending(final String... fieldNames) {
        this.sorts.add(Sorts.descending(fieldNames));
        return this;
    }

    public Bson getSort() {
        if (ObjectUtils.isEmpty(sorts)) {
            return null;
        } else if (sorts.size() == 1) {
            return sorts.get(0);
        } else {
            return Sorts.orderBy(sorts);
        }
    }

    public MongoFilter page(Page page) {
        this.page = page;
        return this;
    }

    public MongoFilter page(int pageNum, int pageSize) {
        this.page = new Page(pageNum, pageSize);
        return this;
    }

    public boolean hasSort() {
        return ObjectUtils.isEmpty(sorts);
    }

    public boolean hasPage() {
        return Objects.nonNull(page);
    }

    public MongoFilter andByCreatorId(String id) {
        this.filter = Filters.and(filter, Filters.eq(BaseAuditableEntity.CREATED_BY, new ObjectId(id)));
        return this;
    }

}
