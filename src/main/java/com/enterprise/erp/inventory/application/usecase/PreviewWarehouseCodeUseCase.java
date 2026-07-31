package com.enterprise.erp.inventory.application.usecase;

import com.enterprise.erp.inventory.application.dto.WarehouseCodePreviewResponse;
import com.enterprise.erp.inventory.infrastructure.persistence.WarehouseJpaRepository;
import com.enterprise.erp.shared.util.WarehouseCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PreviewWarehouseCodeUseCase {

    private final WarehouseJpaRepository warehouseJpaRepository;

    @Transactional(readOnly = true)
    public WarehouseCodePreviewResponse execute(String name) {
        String code = WarehouseCodeGenerator.generateUniqueCode(name, warehouseJpaRepository::existsByCode);
        return new WarehouseCodePreviewResponse(code);
    }
}
