package com.empresa.erp.auth.infrastructure.controller;

import com.empresa.erp.auth.application.dto.*;
import com.empresa.erp.auth.application.usecase.*;
import com.empresa.erp.usuarios.application.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final LogoutUseCase logoutUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final RequestPasswordResetUseCase requestPasswordResetUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final GetProfileUseCase getProfileUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final SessionActivityUseCase sessionActivityUseCase;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(loginUseCase.execute(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request,
                                       @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        logoutUseCase.execute(request, authorizationHeader);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar tokens")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(refreshTokenUseCase.execute(request));
    }

    @PostMapping("/session/activity")
    @Operation(summary = "Registrar actividad de sesión y renovar token si corresponde")
    public ResponseEntity<SessionActivityResponse> sessionActivity(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody SessionActivityRequest request) {
        return ResponseEntity.ok(sessionActivityUseCase.execute(user.getUsername(), request));
    }

    @PostMapping("/password-reset/request")
    @Operation(summary = "Solicitar recuperación de contraseña")
    public ResponseEntity<PasswordResetResponseDto> requestReset(@Valid @RequestBody PasswordResetRequestDto request) {
        return ResponseEntity.ok(requestPasswordResetUseCase.execute(request));
    }

    @PostMapping("/password-reset/confirm")
    @Operation(summary = "Confirmar recuperación de contraseña")
    public ResponseEntity<Void> confirmReset(@Valid @RequestBody PasswordResetConfirmDto request) {
        resetPasswordUseCase.execute(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/change-password")
    @Operation(summary = "Cambiar contraseña")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal UserDetails user,
                                               @Valid @RequestBody ChangePasswordRequest request) {
        changePasswordUseCase.execute(user.getUsername(), request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @Operation(summary = "Obtener perfil del usuario autenticado")
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(getProfileUseCase.execute(user.getUsername()));
    }

    @PutMapping("/profile")
    @Operation(summary = "Actualizar perfil del usuario autenticado")
    public ResponseEntity<UserResponse> updateProfile(@AuthenticationPrincipal UserDetails user,
                                                      @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(updateProfileUseCase.execute(user.getUsername(), request));
    }
}
