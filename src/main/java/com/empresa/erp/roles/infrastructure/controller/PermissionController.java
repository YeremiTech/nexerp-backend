package com.empresa.erp.roles.infrastructure.controller;

import com.empresa.erp.roles.application.dto.PermissionResponse;
import com.empresa.erp.roles.application.usecase.ListPermissionsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@Tag(name = "Permissions")
public class PermissionController {

    private final ListPermissionsUseCase listPermissionsUseCase;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_READ')")
    @Operation(summary = "Listar permisos del sistema")
    public ResponseEntity<List<PermissionResponse>> list() {
        return ResponseEntity.ok(listPermissionsUseCase.execute());
    }
}
