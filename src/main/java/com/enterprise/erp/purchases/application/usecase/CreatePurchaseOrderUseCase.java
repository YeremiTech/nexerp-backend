package com.enterprise.erp.purchases.application.usecase;

import com.enterprise.erp.purchases.application.dto.CreatePurchaseOrderRequest;
import com.enterprise.erp.purchases.application.dto.PurchaseOrderResponse;
import com.enterprise.erp.purchases.application.mapper.PurchaseOrderMapper;
import com.enterprise.erp.purchases.infrastructure.persistence.PurchaseOrderJpaEntity;
import com.enterprise.erp.purchases.infrastructure.persistence.PurchaseOrderJpaRepository;
import com.enterprise.erp.purchases.infrastructure.persistence.PurchaseOrderLineJpaEntity;
import com.enterprise.erp.products.infrastructure.persistence.ProductJpaRepository;
import com.enterprise.erp.suppliers.infrastructure.persistence.SupplierJpaRepository;
import com.enterprise.erp.shared.application.constants.ApiErrorCode;
import com.enterprise.erp.shared.domain.exception.BusinessRuleException;
import com.enterprise.erp.shared.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CreatePurchaseOrderUseCase {

    private final PurchaseOrderJpaRepository purchaseOrderJpaRepository;
    private final SupplierJpaRepository supplierJpaRepository;
    private final ProductJpaRepository productJpaRepository;
    private final PurchaseOrderMapper purchaseOrderMapper;

    @Transactional
    public PurchaseOrderResponse execute(CreatePurchaseOrderRequest request) {
        if (request.lines() == null || request.lines().isEmpty()) {
            throw new BusinessRuleException(ApiErrorCode.PURCHASE_ORDER_NO_LINES);
        }

        var supplier = supplierJpaRepository.findById(request.supplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", request.supplierId()));

        BigDecimal total = BigDecimal.ZERO;
        PurchaseOrderJpaEntity order = PurchaseOrderJpaEntity.builder()
                .supplier(supplier)
                .status("DRAFT")
                .total(BigDecimal.ZERO)
                .build();

        for (CreatePurchaseOrderRequest.Line lineReq : request.lines()) {
            var product = productJpaRepository.findById(lineReq.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto", lineReq.productId()));
            order.getLines().add(PurchaseOrderLineJpaEntity.builder()
                    .order(order)
                    .product(product)
                    .quantity(lineReq.quantity())
                    .unitPrice(lineReq.unitPrice())
                    .build());
            total = total.add(lineReq.unitPrice().multiply(BigDecimal.valueOf(lineReq.quantity())));
        }
        order.setTotal(total);

        return purchaseOrderMapper.toResponse(purchaseOrderJpaRepository.save(order));
    }
}
