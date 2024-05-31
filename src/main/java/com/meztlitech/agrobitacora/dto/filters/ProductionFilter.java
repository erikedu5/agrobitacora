package com.meztlitech.agrobitacora.dto.filters;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProductionFilter extends PageFilter {

    public ProductionFilter(int page, int size) {
        super.setPage(page);
        super.setSize(size);
    }
}
