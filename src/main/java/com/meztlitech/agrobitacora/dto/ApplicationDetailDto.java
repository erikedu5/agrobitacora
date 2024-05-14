package com.meztlitech.agrobitacora.dto;

import lombok.Data;

@Data
public class ApplicationDetailDto {

    private Long dosis;

    private String unit;

    private String productName;

    private String activeIngredient;

    private String[] condiciones;
}
