package com.meztlitech.agrobitacora.dto.filters;

import lombok.Data;

@Data
public class NutritionFilter extends PageFilter {

    public NutritionFilter(int page, int size) {
        super.setPage(page);
        super.setSize(size);
    }
}
