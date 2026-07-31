package com.enterprise.erp.categories.application.usecase;

import com.enterprise.erp.categories.application.dto.CategoryResponse;
import com.enterprise.erp.categories.application.mapper.CategoryMapper;
import com.enterprise.erp.categories.infrastructure.persistence.CategoryJpaRepository;
import com.enterprise.erp.infrastructure.config.RedisConfig;
import com.enterprise.erp.shared.domain.exception.ResourceNotFoundException;
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
