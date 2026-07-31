package com.enterprise.erp.purchases.application.usecase;

import com.enterprise.erp.purchases.application.dto.PurchaseOrderResponse;
import com.enterprise.erp.purchases.application.mapper.PurchaseOrderMapper;
import com.enterprise.erp.purchases.infrastructure.persistence.PurchaseOrderJpaRepository;
import com.enterprise.erp.shared.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetPurchaseOrderUseCase {

    private final PurchaseOrderJpaRepository purchaseOrderJpaRepository;
    private final PurchaseOrderMapper purchaseOrderMapper;

    @Transactional(readOnly = true)
    public PurchaseOrderResponse execute(Long id) {
        return purchaseOrderJpaRepository.findById(id)
                .map(purchaseOrderMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de compra", id));
    }
}
