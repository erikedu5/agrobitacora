package com.meztlitech.agrobitacora.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LaborDto {

    private LocalDateTime laborDate;

    private String kindLabor;

    private Long timeLabor;

    private Long workers_number;

    private String description;
}
