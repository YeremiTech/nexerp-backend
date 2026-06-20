package com.empresa.erp.categorias.application.usecase;

import com.empresa.erp.categorias.application.dto.CategoryResponse;
import com.empresa.erp.categorias.application.mapper.CategoryMapper;
import com.empresa.erp.categorias.infrastructure.persistence.CategoryJpaRepository;
import com.empresa.erp.infrastructure.config.RedisConfig;
import com.empresa.erp.shared.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetCategoryUseCase {

    private final CategoryJpaRepository categoryJpaRepository;
    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    @Cacheable(value = RedisConfig.CACHE_CATEGORIES, key = "#id")
    public CategoryResponse execute(Long id) {
        return categoryJpaRepository.findById(id)
                .map(categoryMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", id));
    }
}
