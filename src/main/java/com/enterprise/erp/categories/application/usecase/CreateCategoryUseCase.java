package com.enterprise.erp.categories.application.usecase;

import com.enterprise.erp.categories.application.dto.CategoryResponse;
import com.enterprise.erp.categories.application.dto.CreateCategoryRequest;
import com.enterprise.erp.categories.application.mapper.CategoryMapper;
import com.enterprise.erp.categories.infrastructure.persistence.CategoryJpaEntity;
import com.enterprise.erp.categories.infrastructure.persistence.CategoryJpaRepository;
import com.enterprise.erp.infrastructure.config.RedisConfig;
import com.enterprise.erp.shared.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateCategoryUseCase {

    private final CategoryJpaRepository categoryJpaRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    @CacheEvict(value = RedisConfig.CACHE_CATEGORIES, allEntries = true)
    public CategoryResponse execute(CreateCategoryRequest request) {
        CategoryJpaEntity parent = null;
        if (request.parentId() != null) {
            parent = categoryJpaRepository.findById(request.parentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría", request.parentId()));
        }
        CategoryJpaEntity entity = CategoryJpaEntity.builder()
                .name(request.name())
                .parent(parent)
                .active(true)
                .build();
        return categoryMapper.toResponse(categoryJpaRepository.save(entity));
    }
}
