package com.meztlitech.agrobitacora.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class SaleDto {
    @NotNull
    private LocalDateTime saleDate;

    @NotNull
    private Long packages;

    @NotNull
    private Double price;
}
