package com.empresa.erp.clientes.application.usecase;

import com.empresa.erp.clientes.application.dto.ClientResponse;
import com.empresa.erp.clientes.application.dto.CreateClientRequest;
import com.empresa.erp.clientes.application.mapper.ClientMapper;
import com.empresa.erp.clientes.infrastructure.persistence.ClientJpaEntity;
import com.empresa.erp.clientes.infrastructure.persistence.ClientJpaRepository;
import com.empresa.erp.infrastructure.config.RedisConfig;
import com.empresa.erp.shared.util.PeruValidation;
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
