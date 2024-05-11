package com.meztlitech.agrobitacora.dto;

import lombok.Data;

@Data
public class CropDto {

    private String alias;
    private Long latitud;
    private Long longitud;
    private String location;
}
