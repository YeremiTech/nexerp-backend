package com.empresa.erp.shared.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.function.Predicate;

public final class SlugCodeGenerator {

    private SlugCodeGenerator() {
    }

    public static String generateUniqueCode(
            String prefix,
            int maxLength,
            String name,
            String fallbackSlug,
            String entityLabel,
            Predicate<String> codeExists
    ) {
        String slug = slugFromName(name);
        if (slug.isEmpty()) {
            slug = fallbackSlug;
        }

        String candidate = fitLength(prefix + slug, maxLength);
        if (!codeExists.test(candidate)) {
            return candidate;
        }

        for (int sequence = 2; sequence < 1000; sequence++) {
            String suffix = String.format("-%02d", sequence);
            String stem = fitLength(prefix + slug, maxLength - suffix.length());
            candidate = stem + suffix;
            if (!codeExists.test(candidate)) {
                return candidate;
            }
        }

        throw new IllegalStateException("No se pudo generar un codigo unico de " + entityLabel);
    }

    private static String slugFromName(String name) {
        if (name == null || name.isBlank()) {
            return "";
        }
        String normalized = Normalizer.normalize(name.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT);
        return normalized
                .replaceAll("[^A-Z0-9]+", "-")
                .replaceAll("^-+|-+$", "")
                .replaceAll("-{2,}", "-");
    }

    private static String fitLength(String value, int maxLength) {
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength).replaceAll("-+$", "");
    }
}
