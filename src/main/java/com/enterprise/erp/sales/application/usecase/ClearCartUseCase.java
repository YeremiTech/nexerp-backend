package com.enterprise.erp.sales.application.usecase;

import com.enterprise.erp.sales.infrastructure.persistence.SalesCartJpaRepository;
import com.enterprise.erp.shared.domain.exception.ResourceNotFoundException;
import com.enterprise.erp.users.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClearCartUseCase {

    private final SalesCartJpaRepository salesCartJpaRepository;
    private final UserJpaRepository userJpaRepository;

    @Transactional
    public void execute(String username) {
        var user = userJpaRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", username));
        salesCartJpaRepository.findLatestByUserIdWithItems(user.getId())
                .ifPresent(cart -> {
                    cart.getItems().clear();
                    salesCartJpaRepository.save(cart);
                });
    }
}
