package com.meztlitech.agrobitacora.dto;

import java.time.LocalDateTime;

import com.meztlitech.agrobitacora.dto.enums.KindBillAssociated;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@RequiredArgsConstructor
public class BillDto {

    @NotNull
    private LocalDateTime billDate;

    @NotBlank
    private String concept;

    @NotNull
    private Double cost;

    private String pathEvidence;

    private Long idBillAssociated;

    private KindBillAssociated kindBillAssociated;

}
