package com.enterprise.erp.suppliers.application.usecase;

import com.enterprise.erp.infrastructure.config.RedisConfig;
import com.enterprise.erp.suppliers.application.dto.CreateSupplierRequest;
import com.enterprise.erp.suppliers.application.dto.SupplierResponse;
import com.enterprise.erp.suppliers.application.mapper.SupplierMapper;
import com.enterprise.erp.suppliers.infrastructure.persistence.SupplierJpaEntity;
import com.enterprise.erp.suppliers.infrastructure.persistence.SupplierJpaRepository;
import com.enterprise.erp.shared.util.PeruValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateSupplierUseCase {

    private final SupplierJpaRepository supplierJpaRepository;
    private final SupplierMapper supplierMapper;

    @Transactional
    @CacheEvict(value = RedisConfig.CACHE_DASHBOARD, allEntries = true)
    public SupplierResponse execute(CreateSupplierRequest request) {
        String taxId = PeruValidation.normalizeDocument(request.taxId());
        String phone = PeruValidation.normalizePhone(request.phone());
        PeruValidation.validateRuc(taxId);
        PeruValidation.validatePhone(phone);

        SupplierJpaEntity entity = SupplierJpaEntity.builder()
                .name(request.name().trim())
                .taxId(taxId)
                .email(request.email() != null ? request.email().trim() : null)
                .phone(phone)
                .active(true)
                .build();
        return supplierMapper.toResponse(supplierJpaRepository.save(entity));
    }
}
