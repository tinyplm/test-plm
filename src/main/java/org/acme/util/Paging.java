package org.acme.util;

import java.util.List;
import java.util.function.Function;
import java.util.function.LongSupplier;

import org.hibernate.query.SelectionQuery;

public final class Paging {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    private Paging() {}

    // ------------------------------------------------
    // Entity paging
    // ------------------------------------------------
    public static <E> PageResult<E> page(
            SelectionQuery<E> query,
            LongSupplier countSupplier,
            Integer page,
            Integer size) {

        int p = normalizePage(page);
        int s = normalizeSize(size);

        query.setFirstResult(p * s);
        query.setMaxResults(s);

        List<E> items = query.getResultList();
        long total = countSupplier.getAsLong();

        int totalPages =
                (int) Math.ceil((double) total / s);

        return new PageResult<>(
                items,
                p,
                s,
                total,
                totalPages
        );
    }

    // ------------------------------------------------
    // Entity → DTO paging
    // ------------------------------------------------
    public static <E, D> PageResult<D> page(
            SelectionQuery<E> query,
            LongSupplier countSupplier,
            Integer page,
            Integer size,
            Function<E, D> mapper) {

        PageResult<E> result =
                page(query, countSupplier, page, size);

        List<D> mapped =
                result.items()
                        .stream()
                        .map(mapper)
                        .toList();

        return new PageResult<>(
                mapped,
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }

    private static int normalizePage(Integer page) {
        return (page == null || page < 0) ? DEFAULT_PAGE : page;
    }

    private static int normalizeSize(Integer size) {
        if (size == null || size <= 0) return DEFAULT_SIZE;
        return Math.min(size, MAX_SIZE);
    }
}
