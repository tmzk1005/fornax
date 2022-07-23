package zk.fornax.manager.bean;

import java.util.List;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PageData<T> extends Page {

    private List<T> data;

    private long total;

    public PageData(List<T> data, long total, int pageNum, int pageSize) {
        this.data = data;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    @JsonProperty
    public long getTotalPages() {
        long result = total / pageSize;
        long remainder = total % pageSize;
        if (remainder > 0) {
            ++result;
        }
        return result;
    }

    public <V> PageData<V> map(Function<T, V> function) {
        return new PageData<>(data.stream().map(function).toList(), total, pageNum, pageSize);
    }

}
