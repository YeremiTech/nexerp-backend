package com.empresa.erp.inventario.application.usecase;

import com.empresa.erp.inventario.application.dto.CreateWarehouseRequest;
import com.empresa.erp.inventario.application.dto.WarehouseResponse;
import com.empresa.erp.inventario.infrastructure.persistence.WarehouseJpaEntity;
import com.empresa.erp.inventario.infrastructure.persistence.WarehouseJpaRepository;
import com.empresa.erp.shared.util.WarehouseCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateWarehouseUseCase {

    private final WarehouseJpaRepository warehouseJpaRepository;

    @Transactional
    public WarehouseResponse execute(CreateWarehouseRequest request) {
        String name = request.name().trim();
        String code = WarehouseCodeGenerator.generateUniqueCode(name, warehouseJpaRepository::existsByCode);
        WarehouseJpaEntity entity = WarehouseJpaEntity.builder()
                .code(code)
                .name(name)
                .active(true)
                .build();
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
