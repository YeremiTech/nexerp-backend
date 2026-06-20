package com.empresa.erp.compras.application.usecase;

import com.empresa.erp.compras.application.dto.PurchaseOrderResponse;
import com.empresa.erp.compras.application.mapper.PurchaseOrderMapper;
import com.empresa.erp.compras.infrastructure.persistence.PurchaseOrderJpaRepository;
import com.empresa.erp.shared.domain.exception.ResourceNotFoundException;
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
