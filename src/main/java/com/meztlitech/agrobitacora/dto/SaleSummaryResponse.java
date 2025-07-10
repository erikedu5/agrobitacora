package com.meztlitech.agrobitacora.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SaleSummaryResponse {
    private LocalDate date;
    private Double total;
    private List<SaleResponse> sales;
}
