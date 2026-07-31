package com.enterprise.erp.shared.domain.exception;

import com.enterprise.erp.shared.application.constants.ApiErrorCode;

public class ResourceNotFoundException extends DomainException {

    public ResourceNotFoundException(String resource, Object id) {
        super(ApiErrorCode.RESOURCE_NOT_FOUND, resource, id);
    }
}
