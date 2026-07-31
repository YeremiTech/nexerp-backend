package com.enterprise.erp.suppliers.application.usecase;

import com.enterprise.erp.infrastructure.config.RedisConfig;
import com.enterprise.erp.suppliers.application.dto.SupplierResponse;
import com.enterprise.erp.suppliers.application.dto.UpdateSupplierRequest;
import com.enterprise.erp.suppliers.application.mapper.SupplierMapper;
import com.enterprise.erp.suppliers.infrastructure.persistence.SupplierJpaEntity;
import com.enterprise.erp.suppliers.infrastructure.persistence.SupplierJpaRepository;
import com.enterprise.erp.shared.domain.exception.ResourceNotFoundException;
import com.enterprise.erp.shared.util.PeruValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateSupplierUseCase {

    private final SupplierJpaRepository supplierJpaRepository;
    private final SupplierMapper supplierMapper;

    @Transactional
    @CacheEvict(value = RedisConfig.CACHE_DASHBOARD, allEntries = true)
    public SupplierResponse execute(Long id, UpdateSupplierRequest request) {
        SupplierJpaEntity entity = supplierJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", id));
        if (request.name() != null) {
            entity.setName(request.name().trim());
        }
        if (request.taxId() != null) {
            String taxId = PeruValidation.normalizeDocument(request.taxId());
            PeruValidation.validateRuc(taxId);
            entity.setTaxId(taxId);
        }
        if (request.email() != null) {
            entity.setEmail(request.email().trim());
        }
        if (request.phone() != null) {
            String phone = PeruValidation.normalizePhone(request.phone());
            PeruValidation.validatePhone(phone);
            entity.setPhone(phone);
        }
        if (request.active() != null) {
            entity.setActive(request.active());
        }
        return supplierMapper.toResponse(supplierJpaRepository.save(entity));
    }
}
