package com.empresa.erp.shared.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageableSupport {

    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final String CREATED_AT = "createdAt";
    public static final Sort NEWEST_FIRST = Sort.by(Sort.Direction.DESC, CREATED_AT);

    private PageableSupport() {
    }

    /**
     * Aplica orden por fecha de registro descendente cuando el cliente no envía sort.
     * Si el cliente envía sort explícito, se respeta.
     */
    public static Pageable newestFirst(Pageable pageable) {
        if (pageable.getSort().isSorted()) {
            return pageable;
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), NEWEST_FIRST);
    }
}
