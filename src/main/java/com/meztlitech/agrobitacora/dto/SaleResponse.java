package com.meztlitech.agrobitacora.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SaleResponse {
    private Long id;
    private LocalDateTime saleDate;
    private Long packages;
    private Double price;
    /**
     * Flower name for the crop from which the sale was made.
     */
    private String flowerName;
}
