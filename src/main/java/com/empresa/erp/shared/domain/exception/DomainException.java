package com.empresa.erp.shared.domain.exception;

import com.empresa.erp.shared.application.constants.ApiErrorCode;
import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {

    private final String errorCode;

    public DomainException(ApiErrorCode apiErrorCode) {
        super(apiErrorCode.getDefaultMessage());
        this.errorCode = apiErrorCode.getCode();
    }

    public DomainException(ApiErrorCode apiErrorCode, Object... args) {
        super(apiErrorCode.formatMessage(args));
        this.errorCode = apiErrorCode.getCode();
    }

    public DomainException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
