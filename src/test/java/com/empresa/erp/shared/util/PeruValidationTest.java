package com.empresa.erp.shared.util;

import com.empresa.erp.clientes.domain.ClientType;
import com.empresa.erp.shared.application.constants.ApiErrorCode;
import com.empresa.erp.shared.domain.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PeruValidationTest {

    @Test
    void validateClientTaxId_shouldAcceptValidDni() {
        PeruValidation.validateClientTaxId(ClientType.PERSON, "12345678");
        PeruValidation.validateClientTaxId(ClientType.PERSON, "12.345.678");
    }

    @Test
    void validateClientTaxId_shouldRejectInvalidDni() {
        assertThatThrownBy(() -> PeruValidation.validateClientTaxId(ClientType.PERSON, "1234567"))
                .isInstanceOf(BusinessRuleException.class)
                .extracting(ex -> ((BusinessRuleException) ex).getErrorCode())
                .isEqualTo(ApiErrorCode.INVALID_PERU_DNI.getCode());
    }

    @Test
    void validateClientTaxId_shouldAcceptValidRucForCompany() {
        PeruValidation.validateClientTaxId(ClientType.COMPANY, "20123456789");
    }

    @Test
    void validateClientTaxId_shouldRejectInvalidRucForCompany() {
        assertThatThrownBy(() -> PeruValidation.validateClientTaxId(ClientType.COMPANY, "2012345678"))
                .isInstanceOf(BusinessRuleException.class)
                .extracting(ex -> ((BusinessRuleException) ex).getErrorCode())
                .isEqualTo(ApiErrorCode.INVALID_PERU_RUC.getCode());
    }

    @Test
    void validatePhone_shouldAcceptNineDigits() {
        PeruValidation.validatePhone("999888777");
        PeruValidation.validatePhone("999 888 777");
    }

    @Test
    void validatePhone_shouldRejectInvalidLength() {
        assertThatThrownBy(() -> PeruValidation.validatePhone("12345"))
                .isInstanceOf(BusinessRuleException.class)
                .extracting(ex -> ((BusinessRuleException) ex).getErrorCode())
                .isEqualTo(ApiErrorCode.INVALID_PERU_PHONE.getCode());
    }

    @Test
    void validateCurrency_shouldAcceptPenAndUsd() {
        PeruValidation.validateCurrency("pen");
        PeruValidation.validateCurrency("USD");
    }

    @Test
    void validateCurrency_shouldRejectUnknownCurrency() {
        assertThatThrownBy(() -> PeruValidation.validateCurrency("EUR"))
                .isInstanceOf(BusinessRuleException.class)
                .extracting(ex -> ((BusinessRuleException) ex).getErrorCode())
                .isEqualTo(ApiErrorCode.INVALID_PERU_CURRENCY.getCode());
    }

    @Test
    void normalizeDocument_shouldStripNonDigits() {
        assertThat(PeruValidation.normalizeDocument("20-12345678-9")).isEqualTo("20123456789");
    }

    @Test
    void defaultCurrency_shouldFallbackToPen() {
        assertThat(PeruValidation.defaultCurrency(null)).isEqualTo("PEN");
        assertThat(PeruValidation.defaultCurrency("usd")).isEqualTo("USD");
    }
}
