package com.empresa.erp.compras.application.usecase;

import com.empresa.erp.compras.application.dto.CreatePurchaseOrderRequest;
import com.empresa.erp.compras.application.dto.PurchaseOrderResponse;
import com.empresa.erp.compras.application.mapper.PurchaseOrderMapper;
import com.empresa.erp.compras.infrastructure.persistence.PurchaseOrderJpaEntity;
import com.empresa.erp.compras.infrastructure.persistence.PurchaseOrderJpaRepository;
import com.empresa.erp.compras.infrastructure.persistence.PurchaseOrderLineJpaEntity;
import com.empresa.erp.productos.infrastructure.persistence.ProductJpaRepository;
import com.empresa.erp.proveedores.infrastructure.persistence.SupplierJpaRepository;
import com.empresa.erp.shared.application.constants.ApiErrorCode;
import com.empresa.erp.shared.domain.exception.BusinessRuleException;
import com.empresa.erp.shared.domain.exception.ResourceNotFoundException;
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
