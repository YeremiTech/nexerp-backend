package com.empresa.erp.compras.application.usecase;

import com.empresa.erp.compras.application.dto.PurchaseOrderListItem;
import com.empresa.erp.compras.application.mapper.PurchaseOrderMapper;
import com.empresa.erp.compras.infrastructure.persistence.PurchaseOrderJpaRepository;
import com.empresa.erp.shared.util.PageableSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListPurchaseOrdersUseCase {

    private final PurchaseOrderJpaRepository purchaseOrderJpaRepository;
    private final PurchaseOrderMapper purchaseOrderMapper;

    @Transactional(readOnly = true)
    public Page<PurchaseOrderListItem> execute(String status, Pageable pageable) {
        pageable = PageableSupport.newestFirst(pageable);
        var page = status == null || status.isBlank()
                ? purchaseOrderJpaRepository.findAll(pageable)
                : purchaseOrderJpaRepository.findByStatus(status, pageable);
        return page.map(purchaseOrderMapper::toListItem);
    }
}
