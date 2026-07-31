package com.enterprise.erp.roles.infrastructure.controller;

import com.enterprise.erp.roles.application.dto.AssignPermissionsRequest;
import com.enterprise.erp.roles.application.dto.CreateRoleRequest;
import com.enterprise.erp.roles.application.dto.RoleResponse;
import com.enterprise.erp.roles.application.dto.UpdateRoleRequest;
import com.enterprise.erp.roles.application.usecase.*;
import com.enterprise.erp.shared.application.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "Roles")
public class RoleController {

    private final ListRolesUseCase listRolesUseCase;
    private final CreateRoleUseCase createRoleUseCase;
    private final AssignPermissionsUseCase assignPermissionsUseCase;
    private final GetRoleUseCase getRoleUseCase;
    private final UpdateRoleUseCase updateRoleUseCase;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_READ')")
    @Operation(summary = "Listar roles")
    public ResponseEntity<PageResponse<RoleResponse>> list(
            Pageable pageable) {
        return ResponseEntity.ok(PageResponse.from(listRolesUseCase.execute(pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_READ')")
    @Operation(summary = "Obtener rol")
    public ResponseEntity<RoleResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(getRoleUseCase.execute(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_WRITE')")
    @Operation(summary = "Crear rol")
    public ResponseEntity<RoleResponse> create(@Valid @RequestBody CreateRoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createRoleUseCase.execute(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_WRITE')")
    @Operation(summary = "Actualizar rol")
    public ResponseEntity<RoleResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody UpdateRoleRequest request) {
        return ResponseEntity.ok(updateRoleUseCase.execute(id, request));
    }

    @PutMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('ROLE_WRITE')")
    @Operation(summary = "Asignar permisos a rol")
    public ResponseEntity<RoleResponse> assignPermissions(@PathVariable Long id,
                                                            @Valid @RequestBody AssignPermissionsRequest request) {
        return ResponseEntity.ok(assignPermissionsUseCase.execute(id, request));
    }

    @PatchMapping("/{id}/active")
    @PreAuthorize("hasAuthority('ROLE_WRITE')")
    @Operation(summary = "Actualizar estado del rol")
    public ResponseEntity<RoleResponse> setActive(@PathVariable Long id, @RequestParam boolean active) {
        return ResponseEntity.ok(updateRoleUseCase.execute(id, new UpdateRoleRequest(null, null, active)));
    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasAuthority('ROLE_WRITE')")
    @Operation(summary = "Activar rol")
    public ResponseEntity<RoleResponse> activate(@PathVariable Long id) {
        return ResponseEntity.ok(updateRoleUseCase.execute(id, new UpdateRoleRequest(null, null, true)));
    }

    @PatchMapping("/{id}/inactivar")
    @PreAuthorize("hasAuthority('ROLE_WRITE')")
    @Operation(summary = "Inactivar rol")
    public ResponseEntity<RoleResponse> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(updateRoleUseCase.execute(id, new UpdateRoleRequest(null, null, false)));
    }
}
