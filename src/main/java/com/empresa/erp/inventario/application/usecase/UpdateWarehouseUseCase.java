package com.empresa.erp.inventario.application.usecase;

import com.empresa.erp.inventario.application.dto.UpdateWarehouseRequest;
import com.empresa.erp.inventario.application.dto.WarehouseResponse;
import com.empresa.erp.inventario.infrastructure.persistence.WarehouseJpaEntity;
import com.empresa.erp.inventario.infrastructure.persistence.WarehouseJpaRepository;
import com.empresa.erp.shared.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateWarehouseUseCase {

    private final WarehouseJpaRepository warehouseJpaRepository;

    @Transactional
    public WarehouseResponse execute(Long id, UpdateWarehouseRequest request) {
        WarehouseJpaEntity entity = warehouseJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Almacén", id));
        if (request.name() != null) {
            entity.setName(request.name().trim());
        }
        if (request.active() != null) {
            entity.setActive(request.active());
        }
        WarehouseJpaEntity saved = warehouseJpaRepository.save(entity);
        return new WarehouseResponse(
                saved.getId(),
                saved.getCode(),
                saved.getName(),
                saved.isActive(),
                saved.getCreatedAt(),
                com.empresa.erp.shared.util.ApiDisplayFormatter.formatDateTime(saved.getCreatedAt())
        );
    }
}
