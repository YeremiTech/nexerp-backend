package com.enterprise.erp.purchases.application.usecase;

import com.enterprise.erp.purchases.application.dto.PurchaseOrderListItem;
import com.enterprise.erp.purchases.application.mapper.PurchaseOrderMapper;
import com.enterprise.erp.purchases.infrastructure.persistence.PurchaseOrderJpaRepository;
import com.enterprise.erp.shared.util.PageableSupport;
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
