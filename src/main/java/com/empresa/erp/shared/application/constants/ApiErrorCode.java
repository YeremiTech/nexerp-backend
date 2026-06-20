package com.empresa.erp.shared.application.constants;

/**
 * Códigos y mensajes estándar de error de la API.
 */
public enum ApiErrorCode {

    VALIDATION_FAILED("ERP-400-000", "Datos inválidos"),
    UNAUTHORIZED("ERP-401-000", "Credenciales inválidas"),
    FORBIDDEN("ERP-403-000", "Acceso denegado"),
    RESOURCE_NOT_FOUND("ERP-404-000", "%s no encontrado: %s"),
    INTERNAL_ERROR("ERP-500-000", "Error interno del servidor"),

    SKU_ALREADY_EXISTS("ERP-PROD-001", "SKU ya existe"),
    EMPTY_CART("ERP-SALE-001", "Carrito vacío"),
    PRODUCT_NO_ACTIVE_PRICE("ERP-SALE-002", "El producto no tiene precio vigente"),
    PURCHASE_ORDER_INVALID_RECEIVE("ERP-PUR-001", "La orden no puede recibirse en su estado actual"),
    PURCHASE_ORDER_NO_LINES("ERP-PUR-002", "La orden debe tener al menos una línea"),
    WAREHOUSE_SAME_TRANSFER("ERP-INV-001", "Almacén origen y destino deben ser diferentes"),
    INSUFFICIENT_STOCK("ERP-INV-002", "Stock insuficiente"),
    WAREHOUSE_CODE_ALREADY_EXISTS("ERP-INV-003", "Código de almacén ya existe"),
    INVALID_PERU_DNI("ERP-PERU-001", "El DNI debe tener 8 dígitos"),
    INVALID_PERU_RUC("ERP-PERU-002", "El RUC debe tener 11 dígitos"),
    INVALID_PERU_PHONE("ERP-PERU-003", "El teléfono debe tener 9 dígitos"),
    INVALID_PERU_CURRENCY("ERP-PERU-004", "Moneda no permitida. Use PEN o USD"),
    ROLE_ALREADY_EXISTS("ERP-ROLE-001", "Rol ya existe"),
    USERNAME_ALREADY_EXISTS("ERP-USER-001", "Username ya existe"),
    EMAIL_ALREADY_EXISTS("ERP-USER-002", "Email ya existe"),
    AUTH_USER_NOT_FOUND("ERP-AUTH-001", "Usuario no encontrado"),
    AUTH_WRONG_PASSWORD("ERP-AUTH-002", "Contraseña actual incorrecta"),
    AUTH_TOKEN_INVALID("ERP-AUTH-003", "Token inválido"),
    AUTH_TOKEN_EXPIRED("ERP-AUTH-004", "Token expirado"),
    AUTH_REFRESH_INVALID("ERP-AUTH-005", "Refresh token inválido"),
    AUTH_REFRESH_EXPIRED("ERP-AUTH-006", "Refresh token expirado"),
    AUTH_SESSION_IDLE_TIMEOUT("ERP-AUTH-007", "Sesión cerrada por inactividad");

    private final String code;
    private final String messageTemplate;

    ApiErrorCode(String code, String messageTemplate) {
        this.code = code;
        this.messageTemplate = messageTemplate;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return messageTemplate;
    }

    public String formatMessage(Object... args) {
        if (args == null || args.length == 0) {
            return messageTemplate;
        }
        return String.format(messageTemplate, args);
    }
}
