package org.acme.util;

import java.util.List;

import io.quarkus.hibernate.panache.PanacheQuery;

public final class Paging {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    private Paging() {}

    public static <E> PageResult<E> page(
            PanacheQuery<E, ?, ?, ?> query,
            Integer page,
            Integer size) {

        int p = normalizePage(page);
        int s = normalizeSize(size);

        query.page(p, s);

        var items = query.list();   // ← IMPORTANT
        long total = (long) query.count();

        return PageResult.of((List) items, p, s, total);
    }


    private static int normalizePage(Integer page) {
        return (page == null || page < 0) ? DEFAULT_PAGE : page;
    }

    private static int normalizeSize(Integer size) {
        if (size == null || size <= 0) return DEFAULT_SIZE;
        return Math.min(size, MAX_SIZE);
    }
}
