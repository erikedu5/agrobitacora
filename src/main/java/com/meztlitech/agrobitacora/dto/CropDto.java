package com.meztlitech.agrobitacora.dto;

import lombok.Data;

@Data
public class CropDto {

    private String alias;
    private Double latitud;
    private Double longitud;
    private String location;
    private String flowerName;
    private Long area;
    private Long numberPlants;

}
