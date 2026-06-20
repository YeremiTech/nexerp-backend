package com.empresa.erp.categorias.application.usecase;

import com.empresa.erp.categorias.application.dto.CategoryResponse;
import com.empresa.erp.categorias.application.mapper.CategoryMapper;
import com.empresa.erp.categorias.infrastructure.persistence.CategoryJpaRepository;
import com.empresa.erp.infrastructure.config.RedisConfig;
import com.empresa.erp.shared.util.PageableSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListCategoriesUseCase {

    private final CategoryJpaRepository categoryJpaRepository;
    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    @Cacheable(value = RedisConfig.CACHE_CATEGORIES, key = "'page-' + #pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort + '-a' + (#active == null ? '' : #active)")
    public Page<CategoryResponse> execute(Boolean active, Pageable pageable) {
        pageable = PageableSupport.newestFirst(pageable);
        return categoryJpaRepository.search(active, pageable).map(categoryMapper::toResponse);
    }
}
