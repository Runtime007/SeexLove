package com.chat.seecolove.bean;

/**
 * Created by Administrator on 2017/10/25.
 */

public class PageBean {
    private int pageNo;//": 0,
    private int pageSize;//": 0,
    private int totalCounts;//": 5,
    private int totalPages;//: 1


    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalCounts() {
        return totalCounts;
    }

    public void setTotalCounts(int totalCounts) {
        this.totalCounts = totalCounts;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
