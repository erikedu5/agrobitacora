package com.meztlitech.agrobitacora.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class BillSummaryResponse {
    private LocalDate startDate;
    private LocalDate endDate;
    private Double total;
    private List<BillResponse> bills;
}
