package com.meztlitech.agrobitacora.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class ProductionDetailDto {

    @NotBlank
    private String descriptionProduct;

    @NotNull
    private Long productionNumber;

    @NotBlank
    private String annotation;
}
