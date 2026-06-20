package com.empresa.erp.categorias.application.usecase;

import com.empresa.erp.categorias.application.dto.CategoryResponse;
import com.empresa.erp.categorias.application.dto.CreateCategoryRequest;
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
