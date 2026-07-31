package com.enterprise.erp.clients.application.usecase;

import com.enterprise.erp.clients.application.dto.ClientResponse;
import com.enterprise.erp.clients.application.dto.CreateClientRequest;
import com.enterprise.erp.clients.application.mapper.ClientMapper;
import com.enterprise.erp.clients.infrastructure.persistence.ClientJpaEntity;
import com.enterprise.erp.clients.infrastructure.persistence.ClientJpaRepository;
import com.enterprise.erp.infrastructure.config.RedisConfig;
import com.enterprise.erp.shared.util.PeruValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateClientUseCase {

    private final ClientJpaRepository clientJpaRepository;
    private final ClientMapper clientMapper;

    @Transactional
    @CacheEvict(value = RedisConfig.CACHE_DASHBOARD, allEntries = true)
    public ClientResponse execute(CreateClientRequest request) {
        String taxId = PeruValidation.normalizeDocument(request.taxId());
        String phone = PeruValidation.normalizePhone(request.phone());
        PeruValidation.validateClientTaxId(request.type(), taxId);
        PeruValidation.validatePhone(phone);

        ClientJpaEntity entity = ClientJpaEntity.builder()
                .type(request.type())
                .name(request.name().trim())
                .taxId(taxId)
                .email(request.email() != null ? request.email().trim() : null)
                .phone(phone)
                .active(true)
                .build();
        return clientMapper.toResponse(clientJpaRepository.save(entity));
    }
}
