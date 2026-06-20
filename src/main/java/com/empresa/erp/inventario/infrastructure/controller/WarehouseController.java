package com.empresa.erp.inventario.infrastructure.controller;

import com.empresa.erp.inventario.application.dto.CreateWarehouseRequest;
import com.empresa.erp.inventario.application.dto.UpdateWarehouseRequest;
import com.empresa.erp.inventario.application.dto.WarehouseResponse;
import com.empresa.erp.inventario.application.dto.WarehouseCodePreviewResponse;
import com.empresa.erp.inventario.application.usecase.CreateWarehouseUseCase;
import com.empresa.erp.inventario.application.usecase.GetWarehouseUseCase;
import com.empresa.erp.inventario.application.usecase.ListWarehousesUseCase;
import com.empresa.erp.inventario.application.usecase.PreviewWarehouseCodeUseCase;
import com.empresa.erp.inventario.application.usecase.UpdateWarehouseUseCase;
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
@RequestMapping("/api/v1/warehouses")
@RequiredArgsConstructor
@Tag(name = "Almacenes")
public class WarehouseController {

    private final ListWarehousesUseCase listWarehousesUseCase;
    private final GetWarehouseUseCase getWarehouseUseCase;
    private final CreateWarehouseUseCase createWarehouseUseCase;
    private final UpdateWarehouseUseCase updateWarehouseUseCase;
    private final PreviewWarehouseCodeUseCase previewWarehouseCodeUseCase;

    @GetMapping
    @PreAuthorize("hasAuthority('INVENTORY_READ')")
    @Operation(summary = "Listar almacenes")
    public ResponseEntity<PageResponse<WarehouseResponse>> list(
            @RequestParam(required = false) Boolean active,
            Pageable pageable) {
        return ResponseEntity.ok(PageResponse.from(listWarehousesUseCase.execute(active, pageable)));
    }

    @GetMapping("/code-preview")
    @PreAuthorize("hasAuthority('INVENTORY_READ')")
    @Operation(summary = "Previsualizar codigo de almacen segun nombre")
    public ResponseEntity<WarehouseCodePreviewResponse> previewCode(@RequestParam String name) {
        return ResponseEntity.ok(previewWarehouseCodeUseCase.execute(name));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('INVENTORY_READ')")
    @Operation(summary = "Obtener almacen")
    public ResponseEntity<WarehouseResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(getWarehouseUseCase.execute(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('INVENTORY_WRITE')")
    @Operation(summary = "Crear almacen")
    public ResponseEntity<WarehouseResponse> create(@Valid @RequestBody CreateWarehouseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createWarehouseUseCase.execute(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('INVENTORY_WRITE')")
    @Operation(summary = "Actualizar almacen")
    public ResponseEntity<WarehouseResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody UpdateWarehouseRequest request) {
        return ResponseEntity.ok(updateWarehouseUseCase.execute(id, request));
    }

    @PatchMapping("/{id}/active")
    @PreAuthorize("hasAuthority('INVENTORY_WRITE')")
    @Operation(summary = "Actualizar estado del almacen")
    public ResponseEntity<WarehouseResponse> setActive(@PathVariable Long id, @RequestParam boolean active) {
        return ResponseEntity.ok(updateWarehouseUseCase.execute(id, new UpdateWarehouseRequest(null, active)));
    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasAuthority('INVENTORY_WRITE')")
    @Operation(summary = "Activar almacen")
    public ResponseEntity<WarehouseResponse> activate(@PathVariable Long id) {
        return ResponseEntity.ok(updateWarehouseUseCase.execute(id, new UpdateWarehouseRequest(null, true)));
    }

    @PatchMapping("/{id}/inactivar")
    @PreAuthorize("hasAuthority('INVENTORY_WRITE')")
    @Operation(summary = "Inactivar almacen")
    public ResponseEntity<WarehouseResponse> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(updateWarehouseUseCase.execute(id, new UpdateWarehouseRequest(null, false)));
    }
}
