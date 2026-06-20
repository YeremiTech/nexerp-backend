package com.empresa.erp.shared.domain.exception;

import com.empresa.erp.shared.application.constants.ApiErrorCode;

public class ResourceNotFoundException extends DomainException {

    public ResourceNotFoundException(String resource, Object id) {
        super(ApiErrorCode.RESOURCE_NOT_FOUND, resource, id);
    }
}
