package com.enterprise.erp.categories.application.usecase;

import com.enterprise.erp.categories.application.dto.CategoryResponse;
import com.enterprise.erp.categories.application.dto.UpdateCategoryRequest;
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
public class UpdateCategoryUseCase {

    private final CategoryJpaRepository categoryJpaRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    @CacheEvict(value = RedisConfig.CACHE_CATEGORIES, allEntries = true)
    public CategoryResponse execute(Long id, UpdateCategoryRequest request) {
        CategoryJpaEntity entity = categoryJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", id));
        if (request.name() != null) {
            entity.setName(request.name());
        }
        if (request.parentId() != null) {
            CategoryJpaEntity parent = categoryJpaRepository.findById(request.parentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría", request.parentId()));
            entity.setParent(parent);
        }
        if (request.active() != null) {
            entity.setActive(request.active());
        }
        return categoryMapper.toResponse(categoryJpaRepository.save(entity));
    }
}
