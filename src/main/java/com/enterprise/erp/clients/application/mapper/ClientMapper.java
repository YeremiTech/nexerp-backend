package com.enterprise.erp.clients.application.mapper;

import com.enterprise.erp.clients.application.dto.ClientResponse;
import com.enterprise.erp.clients.infrastructure.persistence.ClientJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(target = "clientCode", expression = "java(com.enterprise.erp.shared.util.ApiDisplayFormatter.clientCode(entity.getId()))")
    ClientResponse toResponse(ClientJpaEntity entity);
}
