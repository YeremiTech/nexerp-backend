package com.empresa.erp.shared.util;

public final class ListSearchSupport {

    private ListSearchSupport() {
    }

    /** Normaliza texto de búsqueda: trim; vacío → null (sin filtro). */
    public static String normalizeSearch(String search) {
        if (search == null) {
            return null;
        }
        String trimmed = search.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /** Patrón LIKE insensible a mayúsculas para JPQL CONCAT. */
    public static String toLikePattern(String search) {
        String normalized = normalizeSearch(search);
        return normalized == null ? null : "%" + normalized.toLowerCase() + "%";
    }

    public static Long extractNumericIdFromSearch(String search) {
        String normalized = normalizeSearch(search);
        if (normalized == null) {
            return null;
        }
        String digits = normalized.replaceAll("\\D+", "");
        if (digits.isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(digits);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static Boolean parseActiveFilter(Boolean active) {
        return active;
    }
}
