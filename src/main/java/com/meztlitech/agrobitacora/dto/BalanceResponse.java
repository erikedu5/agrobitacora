package com.meztlitech.agrobitacora.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BalanceResponse {
    private LocalDate startDate;
    private LocalDate endDate;
    private Double billsTotal;
    private Double salesTotal;
    private Double balance;
}
