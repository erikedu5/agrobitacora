package com.meztlitech.agrobitacora.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductionResponse {

    private Long id;
    private LocalDateTime productionDate;
    private List<ProductionDetailDto> detailDtoList;
}
