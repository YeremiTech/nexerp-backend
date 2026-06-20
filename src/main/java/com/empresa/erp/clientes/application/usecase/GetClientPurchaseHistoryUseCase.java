package com.empresa.erp.clientes.application.usecase;

import com.empresa.erp.clientes.application.dto.ClientPurchaseHistoryItem;
import com.empresa.erp.clientes.infrastructure.persistence.ClientJpaRepository;
import com.empresa.erp.shared.domain.exception.ResourceNotFoundException;
import com.empresa.erp.shared.util.PageableSupport;
import com.empresa.erp.ventas.infrastructure.persistence.SalesOrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetClientPurchaseHistoryUseCase {

    private final ClientJpaRepository clientJpaRepository;
    private final SalesOrderJpaRepository salesOrderJpaRepository;

    @Transactional(readOnly = true)
    public Page<ClientPurchaseHistoryItem> execute(Long clientId, Pageable pageable) {
        if (!clientJpaRepository.existsById(clientId)) {
            throw new ResourceNotFoundException("Cliente", clientId);
        }
        pageable = PageableSupport.newestFirst(pageable);
        return salesOrderJpaRepository.findByClientId(clientId, pageable)
                .map(order -> new ClientPurchaseHistoryItem(
                        order.getId(),
                        com.empresa.erp.shared.util.ApiDisplayFormatter.orderCode(order.getId()),
                        order.getCreatedAt(),
                        com.empresa.erp.shared.util.ApiDisplayFormatter.formatDateTime(order.getCreatedAt()),
                        order.getStatus(),
                        order.getTotal()));
    }
}
