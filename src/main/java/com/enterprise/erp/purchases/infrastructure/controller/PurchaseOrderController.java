package com.enterprise.erp.purchases.infrastructure.controller;

import com.enterprise.erp.purchases.application.dto.*;
import com.enterprise.erp.purchases.application.usecase.*;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/purchase-orders")
@RequiredArgsConstructor
@Tag(name = "Compras")
public class PurchaseOrderController {

    private final CreatePurchaseOrderUseCase createPurchaseOrderUseCase;
    private final ReceivePurchaseOrderUseCase receivePurchaseOrderUseCase;
    private final ListPurchaseOrdersUseCase listPurchaseOrdersUseCase;
    private final GetPurchaseOrderUseCase getPurchaseOrderUseCase;
    private final UpdatePurchaseOrderUseCase updatePurchaseOrderUseCase;
    private final CancelPurchaseOrderUseCase cancelPurchaseOrderUseCase;
    private final ListPurchaseReceiptsUseCase listPurchaseReceiptsUseCase;

    @GetMapping
    @PreAuthorize("hasAuthority('PURCHASE_READ')")
    @Operation(summary = "Listar ordenes de compra")
    public ResponseEntity<PageResponse<PurchaseOrderListItem>> list(
            @RequestParam(required = false) String status,
            Pageable pageable) {
        return ResponseEntity.ok(PageResponse.from(listPurchaseOrdersUseCase.execute(status, pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PURCHASE_READ')")
    @Operation(summary = "Obtener orden de compra")
    public ResponseEntity<PurchaseOrderResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(getPurchaseOrderUseCase.execute(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PURCHASE_WRITE')")
    @Operation(summary = "Crear orden de compra")
    public ResponseEntity<PurchaseOrderResponse> create(@Valid @RequestBody CreatePurchaseOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createPurchaseOrderUseCase.execute(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PURCHASE_WRITE')")
    @Operation(summary = "Actualizar orden de compra en borrador")
    public ResponseEntity<PurchaseOrderResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CreatePurchaseOrderRequest request) {
        return ResponseEntity.ok(updatePurchaseOrderUseCase.execute(id, request));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('PURCHASE_WRITE')")
    @Operation(summary = "Cancelar orden de compra")
    public ResponseEntity<PurchaseOrderResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(cancelPurchaseOrderUseCase.execute(id));
    }

    @GetMapping("/{id}/receipts")
    @PreAuthorize("hasAuthority('PURCHASE_READ')")
    @Operation(summary = "Listar recepciones de la orden de compra")
    public ResponseEntity<List<PurchaseReceiptResponse>> receipts(@PathVariable Long id) {
        return ResponseEntity.ok(listPurchaseReceiptsUseCase.execute(id));
    }

    @PostMapping("/{id}/receive")
    @PreAuthorize("hasAuthority('PURCHASE_WRITE')")
    @Operation(summary = "Recibir orden de compra")
    public ResponseEntity<PurchaseOrderResponse> receive(@PathVariable Long id,
                                                         @Valid @RequestBody ReceivePurchaseOrderRequest request) {
        return ResponseEntity.ok(receivePurchaseOrderUseCase.execute(id, request));
    }
}
