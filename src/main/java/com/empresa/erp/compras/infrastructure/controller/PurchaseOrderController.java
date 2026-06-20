package com.empresa.erp.compras.infrastructure.controller;

import com.empresa.erp.compras.application.dto.CreatePurchaseOrderRequest;
import com.empresa.erp.compras.application.dto.PurchaseOrderListItem;
import com.empresa.erp.compras.application.dto.PurchaseOrderResponse;
import com.empresa.erp.compras.application.dto.ReceivePurchaseOrderRequest;
import com.empresa.erp.compras.application.usecase.CreatePurchaseOrderUseCase;
import com.empresa.erp.compras.application.usecase.GetPurchaseOrderUseCase;
import com.empresa.erp.compras.application.usecase.ListPurchaseOrdersUseCase;
import com.empresa.erp.compras.application.usecase.ReceivePurchaseOrderUseCase;
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
@RequestMapping("/api/v1/purchase-orders")
@RequiredArgsConstructor
@Tag(name = "Compras")
public class PurchaseOrderController {

    private final CreatePurchaseOrderUseCase createPurchaseOrderUseCase;
    private final ReceivePurchaseOrderUseCase receivePurchaseOrderUseCase;
    private final ListPurchaseOrdersUseCase listPurchaseOrdersUseCase;
    private final GetPurchaseOrderUseCase getPurchaseOrderUseCase;

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

    @PostMapping("/{id}/receive")
    @PreAuthorize("hasAuthority('PURCHASE_WRITE')")
    @Operation(summary = "Recibir orden de compra")
    public ResponseEntity<PurchaseOrderResponse> receive(@PathVariable Long id,
                                                         @Valid @RequestBody ReceivePurchaseOrderRequest request) {
        return ResponseEntity.ok(receivePurchaseOrderUseCase.execute(id, request));
    }
}
