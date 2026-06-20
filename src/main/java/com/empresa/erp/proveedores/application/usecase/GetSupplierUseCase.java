package com.empresa.erp.proveedores.application.usecase;

import com.empresa.erp.proveedores.application.dto.SupplierResponse;
import com.empresa.erp.proveedores.application.mapper.SupplierMapper;
import com.empresa.erp.proveedores.infrastructure.persistence.SupplierJpaRepository;
import com.empresa.erp.shared.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetSupplierUseCase {

    private final SupplierJpaRepository supplierJpaRepository;
    private final SupplierMapper supplierMapper;

    @Transactional(readOnly = true)
    public SupplierResponse execute(Long id) {
        return supplierJpaRepository.findById(id)
                .map(supplierMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", id));
    }
}
