package com.empresa.erp.productos.application.usecase;

import com.empresa.erp.productos.application.dto.ProductSkuPreviewResponse;
import com.empresa.erp.productos.infrastructure.persistence.ProductJpaRepository;
import com.empresa.erp.shared.util.ProductSkuGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PreviewProductSkuUseCase {

    private final ProductJpaRepository productJpaRepository;

    @Transactional(readOnly = true)
    public ProductSkuPreviewResponse execute(String name) {
        String sku = ProductSkuGenerator.generateUniqueSku(name, productJpaRepository::existsBySku);
        return new ProductSkuPreviewResponse(sku);
    }
}
