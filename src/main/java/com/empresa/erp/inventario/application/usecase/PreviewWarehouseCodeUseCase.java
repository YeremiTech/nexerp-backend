package com.empresa.erp.inventario.application.usecase;

import com.empresa.erp.inventario.application.dto.WarehouseCodePreviewResponse;
import com.empresa.erp.inventario.infrastructure.persistence.WarehouseJpaRepository;
import com.empresa.erp.shared.util.WarehouseCodeGenerator;
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
