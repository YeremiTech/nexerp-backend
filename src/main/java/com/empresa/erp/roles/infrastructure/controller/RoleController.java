package com.empresa.erp.roles.infrastructure.controller;

import com.empresa.erp.roles.application.dto.AssignPermissionsRequest;
import com.empresa.erp.roles.application.dto.CreateRoleRequest;
import com.empresa.erp.roles.application.dto.RoleResponse;
import com.empresa.erp.roles.application.usecase.AssignPermissionsUseCase;
import com.empresa.erp.roles.application.usecase.CreateRoleUseCase;
import com.empresa.erp.roles.application.usecase.ListRolesUseCase;
import com.empresa.erp.shared.application.dto.PageResponse;
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

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_READ')")
    @Operation(summary = "Listar roles")
    public ResponseEntity<PageResponse<RoleResponse>> list(
            Pageable pageable) {
        return ResponseEntity.ok(PageResponse.from(listRolesUseCase.execute(pageable)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_WRITE')")
    @Operation(summary = "Crear rol")
    public ResponseEntity<RoleResponse> create(@Valid @RequestBody CreateRoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createRoleUseCase.execute(request));
    }

    @PutMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('ROLE_WRITE')")
    @Operation(summary = "Asignar permisos a rol")
    public ResponseEntity<RoleResponse> assignPermissions(@PathVariable Long id,
                                                            @Valid @RequestBody AssignPermissionsRequest request) {
        return ResponseEntity.ok(assignPermissionsUseCase.execute(id, request));
    }
}
