package com.empresa.erp.categorias.application.mapper;

import com.empresa.erp.categorias.application.dto.CategoryResponse;
import com.empresa.erp.categorias.infrastructure.persistence.CategoryJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = com.empresa.erp.shared.util.ApiDisplayFormatter.class)
public interface CategoryMapper {

    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "createdAtFormatted", expression = "java(ApiDisplayFormatter.formatDateTime(entity.getCreatedAt()))")
    CategoryResponse toResponse(CategoryJpaEntity entity);
}
