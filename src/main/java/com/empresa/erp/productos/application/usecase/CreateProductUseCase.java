package com.empresa.erp.productos.application.usecase;

import com.empresa.erp.categorias.infrastructure.persistence.CategoryJpaRepository;
import com.empresa.erp.infrastructure.config.RedisConfig;
import com.empresa.erp.productos.application.dto.CreateProductRequest;
import com.empresa.erp.productos.application.dto.ProductResponse;
import com.empresa.erp.productos.application.mapper.ProductMapper;
import com.empresa.erp.productos.infrastructure.persistence.*;
import com.empresa.erp.shared.domain.exception.ResourceNotFoundException;
import com.empresa.erp.shared.util.PeruValidation;
import com.empresa.erp.shared.util.ProductSkuGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateProductUseCase {

    private final ProductJpaRepository productJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;
    private final ProductMapper productMapper;

    @Transactional
    @CacheEvict(value = {RedisConfig.CACHE_PRODUCTS, RedisConfig.CACHE_DASHBOARD}, allEntries = true)
    public ProductResponse execute(CreateProductRequest request) {
        PeruValidation.validateCurrency(request.currency());
        String name = request.name().trim();
        String sku = ProductSkuGenerator.generateUniqueSku(name, productJpaRepository::existsBySku);

        var category = request.categoryId() == null ? null
                : categoryJpaRepository.findById(request.categoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Categoría", request.categoryId()));

        ProductJpaEntity entity = ProductJpaEntity.builder()
                .sku(sku)
                .name(name)
                .description(request.description())
                .category(category)
                .minStock(request.minStock())
                .active(true)
                .build();

        ProductPriceJpaEntity price = ProductPriceJpaEntity.builder()
                .product(entity)
                .price(request.price())
                .currency(PeruValidation.defaultCurrency(request.currency()))
                .validFrom(LocalDateTime.now())
                .build();
        entity.getPrices().add(price);

        if (request.imageUrls() != null) {
            int order = 0;
            for (String url : request.imageUrls()) {
                entity.getImages().add(ProductImageJpaEntity.builder()
                        .product(entity)
                        .url(url)
                        .sortOrder(order++)
                        .build());
            }
        }

        return productMapper.toResponse(productJpaRepository.save(entity));
    }
}
