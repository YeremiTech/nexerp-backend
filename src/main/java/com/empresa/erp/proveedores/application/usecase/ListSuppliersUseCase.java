package com.empresa.erp.proveedores.application.usecase;

import com.empresa.erp.proveedores.application.dto.SupplierResponse;
import com.empresa.erp.proveedores.application.mapper.SupplierMapper;
import com.empresa.erp.proveedores.infrastructure.persistence.SupplierJpaRepository;
import com.empresa.erp.shared.util.ListSearchSupport;
import com.empresa.erp.shared.util.PageableSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListSuppliersUseCase {

    private final SupplierJpaRepository supplierJpaRepository;
    private final SupplierMapper supplierMapper;

    @Transactional(readOnly = true)
    public Page<SupplierResponse> execute(String search, Boolean hasTaxId, Boolean active, Pageable pageable) {
        pageable = PageableSupport.newestFirst(pageable);
        return supplierJpaRepository.search(
                ListSearchSupport.toLikePattern(search),
                hasTaxId,
                active,
                pageable
        ).map(supplierMapper::toResponse);
    }
}
