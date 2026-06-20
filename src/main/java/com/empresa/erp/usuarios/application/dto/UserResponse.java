package com.empresa.erp.usuarios.application.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record UserResponse(
        Long id,
        String userCode,
        String username,
        String email,
        boolean active,
        Set<String> roles,
        LocalDateTime createdAt,
        String createdAtFormatted
) {
}
