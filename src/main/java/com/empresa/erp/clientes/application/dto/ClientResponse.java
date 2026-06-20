package com.empresa.erp.clientes.application.dto;

import com.empresa.erp.clientes.domain.ClientType;

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
