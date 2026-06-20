package com.empresa.erp.infrastructure.config;

import com.empresa.erp.shared.application.constants.ApiErrorCode;
import com.empresa.erp.shared.application.dto.ApiErrorResponse;
import com.empresa.erp.shared.domain.exception.BusinessRuleException;
import com.empresa.erp.shared.domain.exception.DomainException;
import com.empresa.erp.shared.domain.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getErrorCode(), ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler({BusinessRuleException.class, DomainException.class})
    public ResponseEntity<ApiErrorResponse> handleBusiness(DomainException ex, HttpServletRequest request) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getErrorCode(), ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        return build(
                HttpStatus.UNAUTHORIZED,
                ApiErrorCode.UNAUTHORIZED.getCode(),
                ApiErrorCode.UNAUTHORIZED.getDefaultMessage(),
                request.getRequestURI());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return build(
                HttpStatus.FORBIDDEN,
                ApiErrorCode.FORBIDDEN.getCode(),
                ApiErrorCode.FORBIDDEN.getDefaultMessage(),
                request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return build(
                HttpStatus.BAD_REQUEST,
                ApiErrorCode.VALIDATION_FAILED.getCode(),
                message,
                request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class)
                .error("Unhandled error on {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ApiErrorCode.INTERNAL_ERROR.getCode(),
                ApiErrorCode.INTERNAL_ERROR.getDefaultMessage(),
                request.getRequestURI());
    }

    private ResponseEntity<ApiErrorResponse> build(HttpStatus status, String code, String message, String path) {
        return ResponseEntity.status(status).body(
                new ApiErrorResponse(
                        LocalDateTime.now(),
                        status.value(),
                        status.getReasonPhrase(),
                        code,
                        message,
                        path));
    }
}
