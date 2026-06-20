package com.empresa.erp.categorias.application.usecase;

import com.empresa.erp.categorias.application.dto.CategoryResponse;
import com.empresa.erp.categorias.application.dto.UpdateCategoryRequest;
import com.empresa.erp.categorias.application.mapper.CategoryMapper;
import com.empresa.erp.categorias.infrastructure.persistence.CategoryJpaEntity;
import com.empresa.erp.categorias.infrastructure.persistence.CategoryJpaRepository;
import com.empresa.erp.infrastructure.config.RedisConfig;
import com.empresa.erp.shared.domain.exception.ResourceNotFoundException;
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
