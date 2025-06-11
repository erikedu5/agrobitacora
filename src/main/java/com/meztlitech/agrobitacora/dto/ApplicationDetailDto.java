package com.meztlitech.agrobitacora.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class ApplicationDetailDto {

    @NotNull
    private Long dosis;

    @NotBlank
    private String unit;

    @NotBlank
    private String productName;

    @NotBlank
    private String activeIngredient;

    private String[] condiciones;
}
