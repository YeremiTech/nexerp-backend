package com.enterprise.erp.purchases.application.usecase;

import com.enterprise.erp.products.infrastructure.persistence.ProductJpaRepository;
import com.enterprise.erp.purchases.application.dto.CreatePurchaseOrderRequest;
import com.enterprise.erp.purchases.application.dto.PurchaseOrderResponse;
import com.enterprise.erp.purchases.application.mapper.PurchaseOrderMapper;
import com.enterprise.erp.purchases.infrastructure.persistence.PurchaseOrderJpaEntity;
import com.enterprise.erp.purchases.infrastructure.persistence.PurchaseOrderJpaRepository;
import com.enterprise.erp.purchases.infrastructure.persistence.PurchaseOrderLineJpaEntity;
import com.enterprise.erp.shared.application.constants.ApiErrorCode;
import com.enterprise.erp.shared.domain.exception.BusinessRuleException;
import com.enterprise.erp.shared.domain.exception.ResourceNotFoundException;
import com.enterprise.erp.suppliers.infrastructure.persistence.SupplierJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UpdatePurchaseOrderUseCase {

    private final PurchaseOrderJpaRepository purchaseOrderJpaRepository;
    private final SupplierJpaRepository supplierJpaRepository;
    private final ProductJpaRepository productJpaRepository;
    private final PurchaseOrderMapper purchaseOrderMapper;

    @Transactional
    public PurchaseOrderResponse execute(Long id, CreatePurchaseOrderRequest request) {
        PurchaseOrderJpaEntity order = purchaseOrderJpaRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de compra", id));
        if (!"DRAFT".equals(order.getStatus())) {
            throw new BusinessRuleException(ApiErrorCode.PURCHASE_ORDER_INVALID_UPDATE);
        }
        if (request.lines() == null || request.lines().isEmpty()) {
            throw new BusinessRuleException(ApiErrorCode.PURCHASE_ORDER_NO_LINES);
        }

        var supplier = supplierJpaRepository.findById(request.supplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", request.supplierId()));

        order.setSupplier(supplier);
        order.getLines().clear();
        BigDecimal total = BigDecimal.ZERO;
        for (CreatePurchaseOrderRequest.Line lineRequest : request.lines()) {
            var product = productJpaRepository.findById(lineRequest.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto", lineRequest.productId()));
            order.getLines().add(PurchaseOrderLineJpaEntity.builder()
                    .order(order)
                    .product(product)
                    .quantity(lineRequest.quantity())
                    .unitPrice(lineRequest.unitPrice())
                    .build());
            total = total.add(lineRequest.unitPrice().multiply(BigDecimal.valueOf(lineRequest.quantity())));
        }
        order.setTotal(total);
        return purchaseOrderMapper.toResponse(purchaseOrderJpaRepository.save(order));
    }
}
