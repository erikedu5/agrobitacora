package com.meztlitech.agrobitacora.dto;

import lombok.Data;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class LaborDto {

    @NotNull
    private LocalDateTime laborDate;

    @NotBlank
    private String kindLabor;

    @NotNull
    private Long timeLabor;

    @NotNull
    private Long workers_number;

    @NotBlank
    private String description;
}
