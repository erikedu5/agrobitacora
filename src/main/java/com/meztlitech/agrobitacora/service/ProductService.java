package com.meztlitech.agrobitacora.service;

import com.meztlitech.agrobitacora.dto.ProductInfo;
import com.meztlitech.agrobitacora.repository.ApplicationDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ApplicationDetailRepository repository;

    public Optional<ProductInfo> search(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }
        return repository.findFirstByProductNameContainingIgnoreCase(name)
                .map(ad -> {
                    ProductInfo info = new ProductInfo();
                    info.setName(ad.getProductName());
                    info.setActiveIngredient(ad.getActiveIngredient());
                    info.setUnit(ad.getUnit());
                    return info;
                });
    }
}
