package com.enterprise.erp.suppliers.application.usecase;

import com.enterprise.erp.suppliers.application.dto.SupplierResponse;
import com.enterprise.erp.suppliers.application.mapper.SupplierMapper;
import com.enterprise.erp.suppliers.infrastructure.persistence.SupplierJpaRepository;
import com.enterprise.erp.shared.domain.exception.ResourceNotFoundException;
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
