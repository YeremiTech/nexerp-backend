package com.enterprise.erp.categories.application.mapper;

import com.enterprise.erp.categories.application.dto.CategoryResponse;
import com.enterprise.erp.categories.infrastructure.persistence.CategoryJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = com.enterprise.erp.shared.util.ApiDisplayFormatter.class)
public interface CategoryMapper {

    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "createdAtFormatted", expression = "java(ApiDisplayFormatter.formatDateTime(entity.getCreatedAt()))")
    CategoryResponse toResponse(CategoryJpaEntity entity);
}
