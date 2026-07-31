package com.enterprise.erp.suppliers.application.mapper;

import com.enterprise.erp.suppliers.application.dto.SupplierResponse;
import com.enterprise.erp.suppliers.infrastructure.persistence.SupplierJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SupplierMapper {

    @Mapping(target = "supplierCode", expression = "java(com.enterprise.erp.shared.util.ApiDisplayFormatter.supplierCode(entity.getId()))")
    SupplierResponse toResponse(SupplierJpaEntity entity);
}
