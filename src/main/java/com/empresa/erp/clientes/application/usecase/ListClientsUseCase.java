package com.empresa.erp.clientes.application.usecase;

import com.empresa.erp.clientes.application.dto.ClientResponse;
import com.empresa.erp.clientes.application.mapper.ClientMapper;
import com.empresa.erp.clientes.domain.ClientType;
import com.empresa.erp.clientes.infrastructure.persistence.ClientJpaRepository;
import com.empresa.erp.shared.util.ListSearchSupport;
import com.empresa.erp.shared.util.PageableSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListClientsUseCase {

    private final ClientJpaRepository clientJpaRepository;
    private final ClientMapper clientMapper;

    @Transactional(readOnly = true)
    public Page<ClientResponse> execute(String search, String type, Boolean active, Pageable pageable) {
        ClientType clientType = parseType(type);
        pageable = PageableSupport.newestFirst(pageable);
        return clientJpaRepository.search(
                ListSearchSupport.toLikePattern(search),
                clientType,
                active,
                pageable
        ).map(clientMapper::toResponse);
    }

    private static ClientType parseType(String type) {
        if (type == null || type.isBlank()) {
            return null;
        }
        return ClientType.valueOf(type);
    }
}
