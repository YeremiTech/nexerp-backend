package com.empresa.erp.usuarios.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record UpdateUserRequest(
        @Email String email,
        @Size(min = 8, max = 100) String password,
        Set<Long> roleIds
) {
}
