package com.enterprise.erp.inventory.infrastructure.controller;

import com.enterprise.erp.inventory.application.dto.*;
import com.enterprise.erp.inventory.application.usecase.*;
import com.enterprise.erp.shared.application.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventario")
public class InventoryController {

    private final EntryInventoryUseCase entryInventoryUseCase;
    private final ExitInventoryUseCase exitInventoryUseCase;
    private final AdjustInventoryUseCase adjustInventoryUseCase;
    private final TransferInventoryUseCase transferInventoryUseCase;
    private final GetKardexUseCase getKardexUseCase;
    private final GetInventoryStockUseCase getInventoryStockUseCase;

    @GetMapping("/stock")
    @PreAuthorize("hasAuthority('INVENTORY_READ')")
    @Operation(summary = "Listar existencias")
    public ResponseEntity<PageResponse<InventoryStockResponse>> stock(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean lowStock,
            Pageable pageable) {
        return ResponseEntity.ok(PageResponse.from(getInventoryStockUseCase.list(
                search, productId, warehouseId, categoryId, lowStock, pageable)));
    }

    @GetMapping("/stock/{productId}")
    @PreAuthorize("hasAuthority('INVENTORY_READ')")
    @Operation(summary = "Listar existencias de un producto")
    public ResponseEntity<PageResponse<InventoryStockResponse>> stockByProduct(
            @PathVariable Long productId,
            Pageable pageable) {
        return ResponseEntity.ok(PageResponse.from(getInventoryStockUseCase.byProduct(productId, pageable)));
    }

    @GetMapping("/stock/{productId}/warehouses/{warehouseId}")
    @PreAuthorize("hasAuthority('INVENTORY_READ')")
    @Operation(summary = "Obtener existencia de un producto en un almacén")
    public ResponseEntity<InventoryStockResponse> stockByProductAndWarehouse(
            @PathVariable Long productId,
            @PathVariable Long warehouseId) {
        return ResponseEntity.ok(getInventoryStockUseCase.byProductAndWarehouse(productId, warehouseId));
    }

    @PostMapping("/entry")
    @PreAuthorize("hasAuthority('INVENTORY_WRITE')")
    @Operation(summary = "Entrada de inventario")
    public ResponseEntity<InventoryMovementResponse> entry(@Valid @RequestBody InventoryMovementRequest request) {
        return ResponseEntity.ok(entryInventoryUseCase.execute(request));
    }

    @PostMapping("/exit")
    @PreAuthorize("hasAuthority('INVENTORY_WRITE')")
    @Operation(summary = "Salida de inventario")
    public ResponseEntity<InventoryMovementResponse> exit(@Valid @RequestBody InventoryMovementRequest request) {
        return ResponseEntity.ok(exitInventoryUseCase.execute(request));
    }

    @PostMapping("/adjust")
    @PreAuthorize("hasAuthority('INVENTORY_WRITE')")
    @Operation(summary = "Ajuste de inventario")
    public ResponseEntity<InventoryMovementResponse> adjust(@Valid @RequestBody InventoryAdjustRequest request) {
        return ResponseEntity.ok(adjustInventoryUseCase.execute(request));
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasAuthority('INVENTORY_WRITE')")
    @Operation(summary = "Transferencia entre almacenes")
    public ResponseEntity<InventoryMovementResponse> transfer(@Valid @RequestBody InventoryTransferRequest request) {
        return ResponseEntity.ok(transferInventoryUseCase.execute(request));
    }

    @GetMapping("/kardex/{productId}")
    @PreAuthorize("hasAuthority('INVENTORY_READ')")
    @Operation(summary = "Kardex de producto")
    public ResponseEntity<PageResponse<InventoryMovementResponse>> kardex(@PathVariable Long productId,
                                                                  @RequestParam(required = false) Long warehouseId,
                                                                  Pageable pageable) {
        return ResponseEntity.ok(PageResponse.from(getKardexUseCase.execute(productId, warehouseId, pageable)));
    }
}
