package com.meztlitech.agrobitacora.dto;

import java.time.LocalDateTime;

import com.meztlitech.agrobitacora.dto.enums.KindBillAssociated;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class BillDto {

    private LocalDateTime billDate;
    private String concept;
    private Double cost;
    private String pathEvidence;
    private Long idBillAssociated;
    private KindBillAssociated kindBillAssociated;

}
