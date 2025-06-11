package com.meztlitech.agrobitacora.dto;

import lombok.Data;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class IrrigationDto {

    @NotNull
    private LocalDateTime date;

    @NotBlank
    private String type;

    @NotNull
    private Long timeInIrrigationInMinutes;

    private String[] conditions;
}
