package com.meztlitech.agrobitacora.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;

@Data
public class ProductionDto {

    @NotNull
    private LocalDateTime productionDate;

    @NotEmpty
    private List<ProductionDetailDto> detailDtoList;

}
