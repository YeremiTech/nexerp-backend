package com.enterprise.erp.products.application.usecase;

import com.enterprise.erp.products.application.dto.ProductSkuPreviewResponse;
import com.enterprise.erp.products.infrastructure.persistence.ProductJpaRepository;
import com.enterprise.erp.shared.util.ProductSkuGenerator;
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
