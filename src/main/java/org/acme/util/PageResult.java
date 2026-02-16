package org.acme.util;

import java.util.List;

public record PageResult<T>(
        List<T> items,
        int page,
        int size,
        long totalElements) {

    public static <T> PageResult<T> of(
            List items,
            int page,
            int size,
            long total) {

        return new PageResult<>(
                List.copyOf(items),
                page,
                size,
                total
        );
    }
}

