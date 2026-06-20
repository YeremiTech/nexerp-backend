package com.empresa.erp.shared.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ListSearchSupportTest {

    @Test
    void normalizeSearch_shouldReturnNullForBlankValues() {
        assertThat(ListSearchSupport.normalizeSearch(null)).isNull();
        assertThat(ListSearchSupport.normalizeSearch("")).isNull();
        assertThat(ListSearchSupport.normalizeSearch("   ")).isNull();
    }

    @Test
    void normalizeSearch_shouldTrimValue() {
        assertThat(ListSearchSupport.normalizeSearch("  acme  ")).isEqualTo("acme");
    }

    @Test
    void toLikePattern_shouldWrapNormalizedSearch() {
        assertThat(ListSearchSupport.toLikePattern("  Acme  ")).isEqualTo("%acme%");
        assertThat(ListSearchSupport.toLikePattern(null)).isNull();
    }

    @Test
    void parseActiveFilter_shouldReturnSameValue() {
        assertThat(ListSearchSupport.parseActiveFilter(true)).isTrue();
        assertThat(ListSearchSupport.parseActiveFilter(null)).isNull();
    }
}
