package com.empresa.erp.clientes.application.usecase;

import com.empresa.erp.clientes.application.dto.ClientResponse;
import com.empresa.erp.clientes.application.mapper.ClientMapper;
import com.empresa.erp.clientes.domain.ClientType;
import com.empresa.erp.clientes.infrastructure.persistence.ClientJpaEntity;
import com.empresa.erp.clientes.infrastructure.persistence.ClientJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListClientsUseCaseTest {

    @Mock
    private ClientJpaRepository clientJpaRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ListClientsUseCase useCase;

    @Test
    void execute_shouldSearchWithLikePatternAndFilters() {
        PageRequest pageable = PageRequest.of(0, 10);
        ClientJpaEntity entity = ClientJpaEntity.builder().id(1L).name("Acme").build();
        ClientResponse response = new ClientResponse(1L, "C-000001", ClientType.COMPANY, "Acme",
                null, null, null, true);
        Page<ClientJpaEntity> page = new PageImpl<>(List.of(entity));
        when(clientJpaRepository.search(eq("%acme%"), eq(ClientType.COMPANY), eq(true), any(Pageable.class)))
                .thenReturn(page);
        when(clientMapper.toResponse(entity)).thenReturn(response);

        Page<ClientResponse> result = useCase.execute("  Acme ", "COMPANY", true, pageable);

        verify(clientJpaRepository).search(eq("%acme%"), eq(ClientType.COMPANY), eq(true), any(Pageable.class));
        assertThat(result.getContent()).containsExactly(response);
    }

    @Test
    void execute_shouldPassNullPatternWhenSearchBlank() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(clientJpaRepository.search(isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(Page.empty(pageable));

        Page<ClientResponse> result = useCase.execute("   ", null, null, pageable);

        assertThat(result.getContent()).isEmpty();
        verify(clientJpaRepository).search(isNull(), isNull(), isNull(), any(Pageable.class));
    }
}
