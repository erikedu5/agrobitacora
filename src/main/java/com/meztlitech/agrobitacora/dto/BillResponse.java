package com.meztlitech.agrobitacora.dto;

import com.meztlitech.agrobitacora.dto.enums.KindBillAssociated;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BillResponse {
    private Long id;
    private LocalDateTime billDate;
    private String concept;
    private Double cost;
    private KindBillAssociated kindBillAssociated;
    private Long idBillAssociated;
}
