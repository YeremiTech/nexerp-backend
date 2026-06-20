package com.empresa.erp.shared.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class ApiDisplayFormatter {

    private static final Locale LOCALE_ES = Locale.forLanguageTag("es-ES");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", LOCALE_ES);
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy", LOCALE_ES);

    private ApiDisplayFormatter() {
    }

    public static String formatDateTime(LocalDateTime value) {
        return value == null ? null : value.format(DATE_TIME);
    }

    public static String formatDate(LocalDate value) {
        return value == null ? null : value.format(DATE);
    }

    public static String userCode(Long id) {
        return id == null ? null : String.format("USR-%06d", id);
    }

    public static String orderCode(Long id) {
        return id == null ? null : String.format("ORD-%06d", id);
    }

    public static String movementCode(Long id) {
        return id == null ? null : String.format("MOV-%06d", id);
    }

    public static String purchaseOrderCode(Long id) {
        return id == null ? null : String.format("PO-%06d", id);
    }

    public static String clientCode(Long id) {
        return id == null ? null : String.format("CLI-%06d", id);
    }

    public static String supplierCode(Long id) {
        return id == null ? null : String.format("SUP-%06d", id);
    }

    public static String categoryCode(Long id) {
        return id == null ? null : String.format("CAT-%06d", id);
    }

    public static String referenceLabel(String referenceType, Long referenceId) {
        if (referenceType == null || referenceType.isBlank()) {
            return null;
        }
        if (referenceId == null) {
            return referenceType;
        }
        return switch (referenceType) {
            case "SALES_ORDER" -> orderCode(referenceId);
            case "PURCHASE_ORDER" -> purchaseOrderCode(referenceId);
            default -> referenceType + " #" + referenceId;
        };
    }
}
