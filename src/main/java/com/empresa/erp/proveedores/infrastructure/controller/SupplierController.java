package com.empresa.erp.proveedores.infrastructure.controller;

import com.empresa.erp.proveedores.application.dto.CreateSupplierRequest;
import com.empresa.erp.proveedores.application.dto.SupplierResponse;
import com.empresa.erp.proveedores.application.dto.UpdateSupplierRequest;
import com.empresa.erp.proveedores.application.usecase.*;
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
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
@Tag(name = "Proveedores")
public class SupplierController {

    private final CreateSupplierUseCase createSupplierUseCase;
    private final UpdateSupplierUseCase updateSupplierUseCase;
    private final GetSupplierUseCase getSupplierUseCase;
    private final ListSuppliersUseCase listSuppliersUseCase;

    @GetMapping
    @PreAuthorize("hasAuthority('SUPPLIER_READ')")
    @Operation(summary = "Listar proveedores")
    public ResponseEntity<PageResponse<SupplierResponse>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean hasTaxId,
            @RequestParam(required = false) Boolean active,
            Pageable pageable) {
        return ResponseEntity.ok(PageResponse.from(listSuppliersUseCase.execute(search, hasTaxId, active, pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SUPPLIER_READ')")
    @Operation(summary = "Obtener proveedor")
    public ResponseEntity<SupplierResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(getSupplierUseCase.execute(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SUPPLIER_WRITE')")
    @Operation(summary = "Crear proveedor")
    public ResponseEntity<SupplierResponse> create(@Valid @RequestBody CreateSupplierRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createSupplierUseCase.execute(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SUPPLIER_WRITE')")
    @Operation(summary = "Actualizar proveedor")
    public ResponseEntity<SupplierResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody UpdateSupplierRequest request) {
        return ResponseEntity.ok(updateSupplierUseCase.execute(id, request));
    }

    @PatchMapping("/{id}/active")
    @PreAuthorize("hasAuthority('SUPPLIER_WRITE')")
    @Operation(summary = "Actualizar estado del proveedor")
    public ResponseEntity<SupplierResponse> setActive(@PathVariable Long id, @RequestParam boolean active) {
        return ResponseEntity.ok(updateSupplierUseCase.execute(id, new UpdateSupplierRequest(null, null, null, null, active)));
    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasAuthority('SUPPLIER_WRITE')")
    @Operation(summary = "Activar proveedor")
    public ResponseEntity<SupplierResponse> activate(@PathVariable Long id) {
        return ResponseEntity.ok(updateSupplierUseCase.execute(id, new UpdateSupplierRequest(null, null, null, null, true)));
    }

    @PatchMapping("/{id}/inactivar")
    @PreAuthorize("hasAuthority('SUPPLIER_WRITE')")
    @Operation(summary = "Inactivar proveedor")
    public ResponseEntity<SupplierResponse> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(updateSupplierUseCase.execute(id, new UpdateSupplierRequest(null, null, null, null, false)));
    }
}
