package com.enterprise.erp.clients.application.usecase;

import com.enterprise.erp.clients.application.dto.ClientResponse;
import com.enterprise.erp.clients.application.mapper.ClientMapper;
import com.enterprise.erp.clients.infrastructure.persistence.ClientJpaRepository;
import com.enterprise.erp.shared.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetClientUseCase {

    private final ClientJpaRepository clientJpaRepository;
    private final ClientMapper clientMapper;

    @Transactional(readOnly = true)
    public ClientResponse execute(Long id) {
        return clientJpaRepository.findById(id)
                .map(clientMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
    }
}
