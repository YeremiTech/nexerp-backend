package com.enterprise.erp.inventory.application.usecase;

import com.enterprise.erp.inventory.application.dto.WarehouseResponse;
import com.enterprise.erp.inventory.infrastructure.persistence.WarehouseJpaRepository;
import com.enterprise.erp.shared.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetWarehouseUseCase {

    private final WarehouseJpaRepository warehouseJpaRepository;

    @Transactional(readOnly = true)
    public WarehouseResponse execute(Long id) {
        return warehouseJpaRepository.findById(id)
                .map(w -> new WarehouseResponse(
                        w.getId(),
                        w.getCode(),
                        w.getName(),
                        w.isActive(),
                        w.getCreatedAt(),
                        com.enterprise.erp.shared.util.ApiDisplayFormatter.formatDateTime(w.getCreatedAt())
                ))
                .orElseThrow(() -> new ResourceNotFoundException("Almacén", id));
    }
}
