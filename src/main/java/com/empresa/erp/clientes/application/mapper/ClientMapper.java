package com.empresa.erp.clientes.application.mapper;

import com.empresa.erp.clientes.application.dto.ClientResponse;
import com.empresa.erp.clientes.infrastructure.persistence.ClientJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(target = "clientCode", expression = "java(com.empresa.erp.shared.util.ApiDisplayFormatter.clientCode(entity.getId()))")
    ClientResponse toResponse(ClientJpaEntity entity);
}
