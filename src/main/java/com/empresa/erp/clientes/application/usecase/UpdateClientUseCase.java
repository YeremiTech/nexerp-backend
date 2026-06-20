package com.empresa.erp.clientes.application.usecase;

import com.empresa.erp.clientes.application.dto.ClientResponse;
import com.empresa.erp.clientes.application.dto.UpdateClientRequest;
import com.empresa.erp.clientes.application.mapper.ClientMapper;
import com.empresa.erp.clientes.domain.ClientType;
import com.empresa.erp.clientes.infrastructure.persistence.ClientJpaEntity;
import com.empresa.erp.clientes.infrastructure.persistence.ClientJpaRepository;
import com.empresa.erp.infrastructure.config.RedisConfig;
import com.empresa.erp.shared.domain.exception.ResourceNotFoundException;
import com.empresa.erp.shared.util.PeruValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateClientUseCase {

    private final ClientJpaRepository clientJpaRepository;
    private final ClientMapper clientMapper;

    @Transactional
    @CacheEvict(value = RedisConfig.CACHE_DASHBOARD, allEntries = true)
    public ClientResponse execute(Long id, UpdateClientRequest request) {
        ClientJpaEntity entity = clientJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));

        ClientType effectiveType = request.type() != null ? request.type() : entity.getType();

        if (request.type() != null) {
            entity.setType(request.type());
        }
        if (request.name() != null) {
            entity.setName(request.name().trim());
        }
        if (request.taxId() != null) {
            String taxId = PeruValidation.normalizeDocument(request.taxId());
            PeruValidation.validateClientTaxId(effectiveType, taxId);
            entity.setTaxId(taxId);
        } else if (request.type() != null) {
            PeruValidation.validateClientTaxId(effectiveType, entity.getTaxId());
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
        return clientMapper.toResponse(clientJpaRepository.save(entity));
    }
}
