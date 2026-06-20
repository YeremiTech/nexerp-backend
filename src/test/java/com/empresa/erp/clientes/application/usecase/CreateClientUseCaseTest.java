package com.empresa.erp.clientes.application.usecase;

import com.empresa.erp.clientes.application.dto.ClientResponse;
import com.empresa.erp.clientes.application.dto.CreateClientRequest;
import com.empresa.erp.clientes.application.mapper.ClientMapper;
import com.empresa.erp.clientes.domain.ClientType;
import com.empresa.erp.clientes.infrastructure.persistence.ClientJpaEntity;
import com.empresa.erp.clientes.infrastructure.persistence.ClientJpaRepository;
import com.empresa.erp.shared.application.constants.ApiErrorCode;
import com.empresa.erp.shared.domain.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateClientUseCaseTest {

    @Mock
    private ClientJpaRepository clientJpaRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private CreateClientUseCase useCase;

    @Test
    void execute_shouldRejectInvalidDni() {
        CreateClientRequest request = new CreateClientRequest(
                ClientType.PERSON,
                "Juan Perez",
                "1234567",
                "juan@example.com",
                "999888777"
        );

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(BusinessRuleException.class)
                .extracting(ex -> ((BusinessRuleException) ex).getErrorCode())
                .isEqualTo(ApiErrorCode.INVALID_PERU_DNI.getCode());
    }

    @Test
    void execute_shouldPersistNormalizedClient() {
        CreateClientRequest request = new CreateClientRequest(
                ClientType.PERSON,
                "  Juan Perez  ",
                "12.345.678",
                " juan@example.com ",
                "999888777"
        );
        ClientJpaEntity saved = ClientJpaEntity.builder().id(10L).name("Juan Perez").build();
        ClientResponse response = new ClientResponse(10L, "C-000010", ClientType.PERSON, "Juan Perez",
                "12345678", "juan@example.com", "999888777", true);
        when(clientJpaRepository.save(any(ClientJpaEntity.class))).thenReturn(saved);
        when(clientMapper.toResponse(saved)).thenReturn(response);

        ClientResponse result = useCase.execute(request);

        ArgumentCaptor<ClientJpaEntity> captor = ArgumentCaptor.forClass(ClientJpaEntity.class);
        verify(clientJpaRepository).save(captor.capture());
        ClientJpaEntity entity = captor.getValue();
        assertThat(entity.getName()).isEqualTo("Juan Perez");
        assertThat(entity.getTaxId()).isEqualTo("12345678");
        assertThat(entity.getPhone()).isEqualTo("999888777");
        assertThat(entity.getEmail()).isEqualTo("juan@example.com");
        assertThat(entity.isActive()).isTrue();
        assertThat(result).isEqualTo(response);
    }
}
