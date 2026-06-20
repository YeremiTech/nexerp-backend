package com.empresa.erp.infrastructure.config;

import com.empresa.erp.shared.application.constants.ApiErrorCode;
import com.empresa.erp.shared.application.dto.ApiErrorResponse;
import com.empresa.erp.shared.domain.exception.BusinessRuleException;
import com.empresa.erp.shared.domain.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/test");
    }

    @Test
    void handleNotFound_shouldReturn404() {
        ResponseEntity<ApiErrorResponse> response = handler.handleNotFound(
                new ResourceNotFoundException("Cliente", 99),
                request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ApiErrorCode.RESOURCE_NOT_FOUND.getCode());
        assertThat(response.getBody().path()).isEqualTo("/api/v1/test");
    }

    @Test
    void handleBusiness_shouldReturn422() {
        ResponseEntity<ApiErrorResponse> response = handler.handleBusiness(
                new BusinessRuleException(ApiErrorCode.INVALID_PERU_DNI),
                request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ApiErrorCode.INVALID_PERU_DNI.getCode());
    }

    @Test
    void handleBadCredentials_shouldReturn401() {
        ResponseEntity<ApiErrorResponse> response = handler.handleBadCredentials(
                new BadCredentialsException("bad"),
                request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ApiErrorCode.UNAUTHORIZED.getCode());
    }

    @Test
    void handleAccessDenied_shouldReturn403() {
        ResponseEntity<ApiErrorResponse> response = handler.handleAccessDenied(
                new AccessDeniedException("denied"),
                request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ApiErrorCode.FORBIDDEN.getCode());
    }
}
