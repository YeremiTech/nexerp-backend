package com.enterprise.erp.shared.domain.exception;

import com.enterprise.erp.shared.application.constants.ApiErrorCode;

public class BusinessRuleException extends DomainException {

    public BusinessRuleException(ApiErrorCode code) {
        super(code);
    }

    public BusinessRuleException(ApiErrorCode code, Object... args) {
        super(code, args);
    }
}
