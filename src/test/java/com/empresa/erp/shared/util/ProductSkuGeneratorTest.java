package com.empresa.erp.shared.util;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ProductSkuGeneratorTest {

    @Test
    void generatesSkuFromName() {
        Set<String> existing = new HashSet<>();

        String sku = ProductSkuGenerator.generateUniqueSku("Laptop HP 15", existing::contains);

        assertThat(sku).isEqualTo("SKU-LAPTOP-HP-15");
    }

    @Test
    void appendsSequenceWhenSkuAlreadyExists() {
        Set<String> existing = new HashSet<>();
        existing.add("SKU-LAPTOP-HP-15");

        String sku = ProductSkuGenerator.generateUniqueSku("Laptop HP 15", existing::contains);

        assertThat(sku).isEqualTo("SKU-LAPTOP-HP-15-02");
    }

    @Test
    void removesAccentsFromName() {
        Set<String> existing = new HashSet<>();

        String sku = ProductSkuGenerator.generateUniqueSku("Café Orgánico", existing::contains);

        assertThat(sku).isEqualTo("SKU-CAFE-ORGANICO");
    }
}
