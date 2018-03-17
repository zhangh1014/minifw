package org.lechisoft.minifw.mybatis;

public class Pageable {

    private Paging paging = new Paging();

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }
}