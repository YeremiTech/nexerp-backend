package com.enterprise.erp.inventory.application.usecase;

import com.enterprise.erp.inventory.application.dto.WarehouseResponse;
import com.enterprise.erp.inventory.infrastructure.persistence.WarehouseJpaRepository;
import com.enterprise.erp.shared.util.PageableSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListWarehousesUseCase {

    private final WarehouseJpaRepository warehouseJpaRepository;

    @Transactional(readOnly = true)
    public Page<WarehouseResponse> execute(Boolean active, Pageable pageable) {
        pageable = PageableSupport.newestFirst(pageable);
        return warehouseJpaRepository.search(active, pageable)
                .map(w -> new WarehouseResponse(
                        w.getId(),
                        w.getCode(),
                        w.getName(),
                        w.isActive(),
                        w.getCreatedAt(),
                        com.enterprise.erp.shared.util.ApiDisplayFormatter.formatDateTime(w.getCreatedAt())
                ));
    }
}
