package com.enterprise.erp.clients.application.usecase;

import com.enterprise.erp.clients.application.dto.ClientResponse;
import com.enterprise.erp.clients.application.mapper.ClientMapper;
import com.enterprise.erp.clients.domain.ClientType;
import com.enterprise.erp.clients.infrastructure.persistence.ClientJpaRepository;
import com.enterprise.erp.shared.util.ListSearchSupport;
import com.enterprise.erp.shared.util.PageableSupport;
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
