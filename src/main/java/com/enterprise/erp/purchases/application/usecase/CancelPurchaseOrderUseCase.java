package com.enterprise.erp.purchases.application.usecase;

import com.enterprise.erp.purchases.application.dto.PurchaseOrderResponse;
import com.enterprise.erp.purchases.application.mapper.PurchaseOrderMapper;
import com.enterprise.erp.purchases.infrastructure.persistence.PurchaseOrderJpaEntity;
import com.enterprise.erp.purchases.infrastructure.persistence.PurchaseOrderJpaRepository;
import com.enterprise.erp.shared.application.constants.ApiErrorCode;
import com.enterprise.erp.shared.domain.exception.BusinessRuleException;
import com.enterprise.erp.shared.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CancelPurchaseOrderUseCase {

    private final PurchaseOrderJpaRepository purchaseOrderJpaRepository;
    private final PurchaseOrderMapper purchaseOrderMapper;

    @Transactional
    public PurchaseOrderResponse execute(Long id) {
        PurchaseOrderJpaEntity order = purchaseOrderJpaRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de compra", id));
        if (!"DRAFT".equals(order.getStatus())) {
            throw new BusinessRuleException(ApiErrorCode.PURCHASE_ORDER_INVALID_CANCEL);
        }
        order.setStatus("CANCELLED");
        return purchaseOrderMapper.toResponse(purchaseOrderJpaRepository.save(order));
    }
}
