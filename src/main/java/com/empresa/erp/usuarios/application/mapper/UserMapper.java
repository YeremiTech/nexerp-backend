package com.empresa.erp.usuarios.application.mapper;

import com.empresa.erp.usuarios.application.dto.UserResponse;
import com.empresa.erp.usuarios.infrastructure.persistence.UserJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "userCode", expression = "java(com.empresa.erp.shared.util.ApiDisplayFormatter.userCode(entity.getId()))")
    @Mapping(target = "createdAtFormatted", expression = "java(com.empresa.erp.shared.util.ApiDisplayFormatter.formatDateTime(entity.getCreatedAt()))")
    @Mapping(target = "roles", expression = "java(mapRoles(entity))")
    UserResponse toResponse(UserJpaEntity entity);

    default java.util.Set<String> mapRoles(UserJpaEntity entity) {
        return entity.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet());
    }
}
