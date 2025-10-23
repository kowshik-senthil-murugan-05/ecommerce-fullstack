package com.project.ecommerce.util;

import java.util.List;

public class PageDetails<T>
{
    public List<T> list;
    public int pageNumber;
    public int pageSize;
    public long totalElements;
    public int totalPages;
    public boolean isLastPage;

    public PageDetails(){}

    public PageDetails(List<T> dtoList, int pgNum, int pgSize, long totElements, int totPages, boolean isLastPage)
    {
        this.list = dtoList;
        this.pageNumber = pgNum;
        this.pageSize = pgSize;
        this.totalElements = totElements;
        this.totalPages = totPages;
        this.isLastPage = isLastPage;
    }

}
