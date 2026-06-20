package com.empresa.erp.shared.util;

import java.util.function.Predicate;

public final class ProductSkuGenerator {

    private static final int MAX_SKU_LENGTH = 50;
    private static final String PREFIX = "SKU-";

    private ProductSkuGenerator() {
    }

    public static String generateUniqueSku(String name, Predicate<String> skuExists) {
        return SlugCodeGenerator.generateUniqueCode(
                PREFIX,
                MAX_SKU_LENGTH,
                name,
                "PRODUCTO",
                "producto",
                skuExists
        );
    }
}
