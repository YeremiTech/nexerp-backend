package com.enterprise.erp.purchases.application.usecase;

import com.enterprise.erp.purchases.application.dto.PurchaseReceiptResponse;
import com.enterprise.erp.purchases.infrastructure.persistence.PurchaseOrderJpaRepository;
import com.enterprise.erp.purchases.infrastructure.persistence.PurchaseReceiptJpaRepository;
import com.enterprise.erp.shared.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListPurchaseReceiptsUseCase {

    private final PurchaseOrderJpaRepository purchaseOrderJpaRepository;
    private final PurchaseReceiptJpaRepository purchaseReceiptJpaRepository;

    @Transactional(readOnly = true)
    public List<PurchaseReceiptResponse> execute(Long orderId) {
        if (!purchaseOrderJpaRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Orden de compra", orderId);
        }
        return purchaseReceiptJpaRepository.findByOrderIdOrderByReceivedAtDesc(orderId).stream()
                .map(receipt -> new PurchaseReceiptResponse(
                        receipt.getId(),
                        receipt.getOrder().getId(),
                        receipt.getReceivedAt()))
                .toList();
    }
}
