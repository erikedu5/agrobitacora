package com.meztlitech.agrobitacora.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class CropDto {

    @NotBlank
    private String alias;

    @NotNull
    private Double latitud;

    @NotNull
    private Double longitud;

    @NotBlank
    private String location;

    @NotBlank
    private String flowerName;

    @NotNull
    private Long area;

    @NotNull
    private Long numberPlants;

}
