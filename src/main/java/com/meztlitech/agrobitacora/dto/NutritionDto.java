package com.meztlitech.agrobitacora.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class NutritionDto {

    private String applicationType;

    private LocalDateTime visitDate;

    private LocalDateTime applicationDate;

    private String detail;

    private List<NutritionDetailDto> appDetails;

}
