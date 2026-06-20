package com.empresa.erp.shared.util;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

class PageableSupportTest {

    @Test
    void newestFirst_shouldApplyCreatedAtDescWhenUnsorted() {
        Pageable pageable = PageRequest.of(0, 20);

        Pageable result = PageableSupport.newestFirst(pageable);

        assertThat(result.getSort()).isEqualTo(PageableSupport.NEWEST_FIRST);
    }

    @Test
    void newestFirst_shouldKeepExplicitSort() {
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "name"));

        Pageable result = PageableSupport.newestFirst(pageable);

        assertThat(result.getSort()).isEqualTo(pageable.getSort());
    }
}
