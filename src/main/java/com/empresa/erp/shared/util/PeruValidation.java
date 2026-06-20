package com.empresa.erp.shared.util;

import com.empresa.erp.clientes.domain.ClientType;
import com.empresa.erp.shared.application.constants.ApiErrorCode;
import com.empresa.erp.shared.domain.exception.BusinessRuleException;

import java.util.Set;

public final class PeruValidation {

    public static final int DNI_LENGTH = 8;
    public static final int RUC_LENGTH = 11;
    public static final int PHONE_LENGTH = 9;
    private static final Set<String> ALLOWED_CURRENCIES = Set.of("PEN", "USD");

    private PeruValidation() {
    }

    public static String digitsOnly(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.replaceAll("\\D", "");
    }

    public static void validateClientTaxId(ClientType type, String taxId) {
        if (taxId == null || taxId.isBlank()) {
            return;
        }
        String digits = digitsOnly(taxId);
        if (type == ClientType.PERSON) {
            if (digits.length() != DNI_LENGTH) {
                throw new BusinessRuleException(ApiErrorCode.INVALID_PERU_DNI);
            }
            return;
        }
        if (digits.length() != RUC_LENGTH) {
            throw new BusinessRuleException(ApiErrorCode.INVALID_PERU_RUC);
        }
    }

    public static void validateRuc(String taxId) {
        if (taxId == null || taxId.isBlank()) {
            return;
        }
        String digits = digitsOnly(taxId);
        if (digits.length() != RUC_LENGTH) {
            throw new BusinessRuleException(ApiErrorCode.INVALID_PERU_RUC);
        }
    }

    public static void validatePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return;
        }
        String digits = digitsOnly(phone);
        if (digits.length() != PHONE_LENGTH) {
            throw new BusinessRuleException(ApiErrorCode.INVALID_PERU_PHONE);
        }
    }

    public static void validateCurrency(String currency) {
        if (currency == null || currency.isBlank()) {
            return;
        }
        if (!ALLOWED_CURRENCIES.contains(currency.toUpperCase())) {
            throw new BusinessRuleException(ApiErrorCode.INVALID_PERU_CURRENCY);
        }
    }

    public static String normalizeDocument(String value) {
        return digitsOnly(value);
    }

    public static String normalizePhone(String value) {
        return digitsOnly(value);
    }

    public static String defaultCurrency(String currency) {
        if (currency == null || currency.isBlank()) {
            return "PEN";
        }
        return currency.toUpperCase();
    }
}
