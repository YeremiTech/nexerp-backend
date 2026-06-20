package com.empresa.erp.roles.application.mapper;

import com.empresa.erp.roles.application.dto.RoleResponse;
import com.empresa.erp.roles.infrastructure.persistence.RoleJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "permissions", expression = "java(mapPermissions(entity))")
    RoleResponse toResponse(RoleJpaEntity entity);

    default java.util.Set<String> mapPermissions(RoleJpaEntity entity) {
        return entity.getPermissions().stream().map(p -> p.getCode()).collect(Collectors.toSet());
    }
}
