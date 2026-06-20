package com.empresa.erp.auth.application.dto;

public record PasswordResetResponseDto(String message) {

    private static final String GENERIC_MESSAGE =
            "Si el email existe, recibirás instrucciones para restablecer tu contraseña";

    public static PasswordResetResponseDto submitted() {
        return new PasswordResetResponseDto(GENERIC_MESSAGE);
    }
}
