package com.meztlitech.agrobitacora.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meztlitech.agrobitacora.model.ProductInfo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ObjectMapper objectMapper;
    private List<ProductInfo> products;

    @PostConstruct
    public void loadProducts() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/products.json")) {
            if (is != null) {
                products = Arrays.asList(objectMapper.readValue(is, ProductInfo[].class));
            } else {
                products = List.of();
            }
        }
    }

    public Optional<ProductInfo> search(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }
        String n = name.toLowerCase();
        return products.stream()
                .filter(p -> p.getName() != null && p.getName().toLowerCase().contains(n))
                .findFirst();
    }
}
