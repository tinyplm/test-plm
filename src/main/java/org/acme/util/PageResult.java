package org.acme.util;

import java.util.List;

public record PageResult<T>(
        List<T> items,
        int page,
        int size,
        long totalElements,
        int totalPages
) {

    public static <T> PageResult<T> of(
            List<T> items,
            int page,
            int size,
            long total,
            int pages) {

        return new PageResult<>(items, page, size, total, pages);
    }
}


