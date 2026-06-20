package com.empresa.erp.productos.application.usecase;

import com.empresa.erp.categorias.infrastructure.persistence.CategoryJpaRepository;
import com.empresa.erp.infrastructure.config.RedisConfig;
import com.empresa.erp.productos.application.dto.ProductResponse;
import com.empresa.erp.productos.application.dto.UpdateProductRequest;
import com.empresa.erp.productos.application.mapper.ProductMapper;
import com.empresa.erp.productos.infrastructure.persistence.*;
import com.empresa.erp.shared.domain.exception.ResourceNotFoundException;
import com.empresa.erp.shared.util.PeruValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UpdateProductUseCase {

    private final ProductJpaRepository productJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;
    private final ProductMapper productMapper;

    @Transactional
    @CacheEvict(value = {RedisConfig.CACHE_PRODUCTS, RedisConfig.CACHE_DASHBOARD}, allEntries = true)
    public ProductResponse execute(Long id, UpdateProductRequest request) {
        ProductJpaEntity entity = productJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));

        if (request.name() != null) {
            entity.setName(request.name());
        }
        if (request.description() != null) {
            entity.setDescription(request.description());
        }
        if (request.categoryId() != null) {
            var category = categoryJpaRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría", request.categoryId()));
            entity.setCategory(category);
        }
        if (request.minStock() != null) {
            entity.setMinStock(request.minStock());
        }
        if (request.active() != null) {
            entity.setActive(request.active());
        }
        if (request.price() != null) {
            PeruValidation.validateCurrency(request.currency());
            entity.getPrices().stream()
                    .filter(p -> p.getValidTo() == null)
                    .forEach(p -> p.setValidTo(LocalDateTime.now()));
            entity.getPrices().add(ProductPriceJpaEntity.builder()
                    .product(entity)
                    .price(request.price())
                    .currency(PeruValidation.defaultCurrency(request.currency()))
                    .validFrom(LocalDateTime.now())
                    .build());
        }
        if (request.imageUrls() != null) {
            entity.getImages().clear();
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
