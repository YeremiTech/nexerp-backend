package com.empresa.erp.productos.application.usecase;

import com.empresa.erp.infrastructure.config.RedisConfig;
import com.empresa.erp.productos.application.dto.ProductResponse;
import com.empresa.erp.productos.application.mapper.ProductMapper;
import com.empresa.erp.productos.infrastructure.persistence.ProductJpaRepository;
import com.empresa.erp.shared.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetProductUseCase {

    private final ProductJpaRepository productJpaRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    @Cacheable(value = RedisConfig.CACHE_PRODUCTS, key = "#id")
    public ProductResponse execute(Long id) {
        return productJpaRepository.findById(id)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
    }
}
