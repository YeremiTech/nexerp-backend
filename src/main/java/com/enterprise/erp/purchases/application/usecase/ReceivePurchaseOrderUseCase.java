package com.enterprise.erp.purchases.application.usecase;

import com.enterprise.erp.purchases.application.dto.PurchaseOrderResponse;
import com.enterprise.erp.purchases.application.dto.ReceivePurchaseOrderRequest;
import com.enterprise.erp.purchases.application.mapper.PurchaseOrderMapper;
import com.enterprise.erp.purchases.infrastructure.persistence.PurchaseOrderJpaEntity;
import com.enterprise.erp.purchases.infrastructure.persistence.PurchaseOrderJpaRepository;
import com.enterprise.erp.purchases.infrastructure.persistence.PurchaseReceiptJpaEntity;
import com.enterprise.erp.purchases.infrastructure.persistence.PurchaseReceiptJpaRepository;
import com.enterprise.erp.inventory.application.dto.InventoryMovementRequest;
import com.enterprise.erp.inventory.application.usecase.EntryInventoryUseCase;
import com.enterprise.erp.shared.application.constants.ApiErrorCode;
import com.enterprise.erp.shared.domain.exception.BusinessRuleException;
import com.enterprise.erp.shared.domain.exception.ResourceNotFoundException;
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
        PurchaseOrderJpaEntity order = purchaseOrderJpaRepository.findByIdForUpdate(orderId)
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
