package com.meztlitech.agrobitacora.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class IrrigationDto {

    private LocalDateTime date;

    private String type;

    private Long timeInIrrigationInMinutes;

    private String[] conditions;
}
