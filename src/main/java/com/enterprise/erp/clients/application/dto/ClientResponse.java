package com.enterprise.erp.clients.application.dto;

import com.enterprise.erp.clients.domain.ClientType;

public record ClientResponse(
        Long id,
        String clientCode,
        ClientType type,
        String name,
        String taxId,
        String email,
        String phone,
        boolean active
) {
}
