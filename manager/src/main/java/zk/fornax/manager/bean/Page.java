package zk.fornax.manager.bean;

import lombok.Getter;

public class Page {

    @Getter
    private int pageNum = 1;

    @Getter
    private int pageSize = 10;

    public Page() {
    }

    public Page(int pageNum, int pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public static Page of(int pageNum, int pageSize) {
        return new Page(pageNum, pageSize);
    }

    public int getOffset() {
        return pageSize * (pageNum - 1);
    }

}
