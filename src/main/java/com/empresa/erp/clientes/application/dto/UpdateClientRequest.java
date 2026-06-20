package com.empresa.erp.clientes.application.dto;

import com.empresa.erp.clientes.domain.ClientType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateClientRequest(
        ClientType type,
        @Size(max = 200) String name,
        @Size(max = 50) String taxId,
        @Email @Size(max = 150) String email,
        @Size(max = 30) String phone,
        Boolean active
) {
}
