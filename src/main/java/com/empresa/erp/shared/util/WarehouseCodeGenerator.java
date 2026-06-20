package com.empresa.erp.shared.util;

import java.util.function.Predicate;

public final class WarehouseCodeGenerator {

    private static final int MAX_CODE_LENGTH = 20;
    private static final String PREFIX = "ALM-";

    private WarehouseCodeGenerator() {
    }

    public static String generateUniqueCode(String name, Predicate<String> codeExists) {
        return SlugCodeGenerator.generateUniqueCode(
                PREFIX,
                MAX_CODE_LENGTH,
                name,
                "ALMACEN",
                "almacen",
                codeExists
        );
    }
}
