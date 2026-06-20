package com.empresa.erp.productos.application.usecase;

import com.empresa.erp.infrastructure.config.RedisConfig;
import com.empresa.erp.productos.application.dto.ProductResponse;
import com.empresa.erp.productos.application.mapper.ProductMapper;
import com.empresa.erp.productos.infrastructure.persistence.ProductJpaRepository;
import com.empresa.erp.shared.util.ListSearchSupport;
import com.empresa.erp.shared.util.PageableSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListProductsUseCase {

    private final ProductJpaRepository productJpaRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    @Cacheable(
            value = RedisConfig.CACHE_PRODUCTS,
            key = "'page-' + #pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort + '-s' + (#search == null ? '' : #search) + '-c' + (#categoryId == null ? '' : #categoryId) + '-a' + (#active == null ? '' : #active)"
    )
    public Page<ProductResponse> execute(String search, Long categoryId, Boolean active, Pageable pageable) {
        pageable = PageableSupport.newestFirst(pageable);
        return productJpaRepository.search(
                ListSearchSupport.toLikePattern(search),
                categoryId,
                active,
                pageable
        ).map(productMapper::toResponse);
    }
}
