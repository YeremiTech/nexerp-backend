package com.empresa.erp.compras.application.usecase;

import com.empresa.erp.compras.application.dto.PurchaseOrderResponse;
import com.empresa.erp.compras.application.dto.ReceivePurchaseOrderRequest;
import com.empresa.erp.compras.application.mapper.PurchaseOrderMapper;
import com.empresa.erp.compras.infrastructure.persistence.PurchaseOrderJpaEntity;
import com.empresa.erp.compras.infrastructure.persistence.PurchaseOrderJpaRepository;
import com.empresa.erp.compras.infrastructure.persistence.PurchaseReceiptJpaEntity;
import com.empresa.erp.compras.infrastructure.persistence.PurchaseReceiptJpaRepository;
import com.empresa.erp.inventario.application.dto.InventoryMovementRequest;
import com.empresa.erp.inventario.application.usecase.EntryInventoryUseCase;
import com.empresa.erp.shared.application.constants.ApiErrorCode;
import com.empresa.erp.shared.domain.exception.BusinessRuleException;
import com.empresa.erp.shared.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReceivePurchaseOrderUseCase {

    private final PurchaseOrderJpaRepository purchaseOrderJpaRepository;
    private final PurchaseReceiptJpaRepository purchaseReceiptJpaRepository;
    private final EntryInventoryUseCase entryInventoryUseCase;
    private final PurchaseOrderMapper purchaseOrderMapper;

    @Transactional
    public PurchaseOrderResponse execute(Long orderId, ReceivePurchaseOrderRequest request) {
        PurchaseOrderJpaEntity order = purchaseOrderJpaRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de compra", orderId));

        if ("RECEIVED".equals(order.getStatus()) || "CANCELLED".equals(order.getStatus())) {
            throw new BusinessRuleException(ApiErrorCode.PURCHASE_ORDER_INVALID_RECEIVE);
        }

        for (var line : order.getLines()) {
            entryInventoryUseCase.execute(new InventoryMovementRequest(
                    line.getProduct().getId(),
                    request.warehouseId(),
                    line.getQuantity(),
                    "PURCHASE_ORDER",
                    orderId));
        }

        order.setStatus("RECEIVED");
        purchaseReceiptJpaRepository.save(PurchaseReceiptJpaEntity.builder()
                .order(order)
                .receivedAt(LocalDateTime.now())
                .build());

        return purchaseOrderMapper.toResponse(purchaseOrderJpaRepository.save(order));
    }
}
