package com.empresa.erp.clientes.application.usecase;

import com.empresa.erp.clientes.application.dto.ClientResponse;
import com.empresa.erp.clientes.application.mapper.ClientMapper;
import com.empresa.erp.clientes.infrastructure.persistence.ClientJpaRepository;
import com.empresa.erp.shared.domain.exception.ResourceNotFoundException;
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
