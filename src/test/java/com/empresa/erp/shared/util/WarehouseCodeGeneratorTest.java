package com.empresa.erp.shared.util;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class WarehouseCodeGeneratorTest {

    @Test
    void generatesCodeFromName() {
        Set<String> existing = new HashSet<>();

        String code = WarehouseCodeGenerator.generateUniqueCode("Almacen Central Lima", existing::contains);

        assertThat(code).isEqualTo("ALM-ALMACEN-CENTRAL");
    }

    @Test
    void appendsSequenceWhenCodeAlreadyExists() {
        Set<String> existing = new HashSet<>();
        existing.add("ALM-ALMACEN-CENTRAL");

        String code = WarehouseCodeGenerator.generateUniqueCode("Almacen Central", existing::contains);

        assertThat(code).isEqualTo("ALM-ALMACEN-CENTR-02");
    }

    @Test
    void removesAccentsFromName() {
        Set<String> existing = new HashSet<>();

        String code = WarehouseCodeGenerator.generateUniqueCode("Almacén Norte", existing::contains);

        assertThat(code).isEqualTo("ALM-ALMACEN-NORTE");
    }
}
