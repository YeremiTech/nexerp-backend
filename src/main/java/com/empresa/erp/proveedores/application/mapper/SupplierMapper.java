package com.empresa.erp.proveedores.application.mapper;

import com.empresa.erp.proveedores.application.dto.SupplierResponse;
import com.empresa.erp.proveedores.infrastructure.persistence.SupplierJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SupplierMapper {

    @Mapping(target = "supplierCode", expression = "java(com.empresa.erp.shared.util.ApiDisplayFormatter.supplierCode(entity.getId()))")
    SupplierResponse toResponse(SupplierJpaEntity entity);
}
