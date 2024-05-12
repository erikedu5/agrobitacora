package com.meztlitech.agrobitacora.dto.filters;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class IrrigationFilter extends PageFilter {
    public IrrigationFilter(int page, int size) {
        super.setPage(page);
        super.setSize(size);
    }
}
