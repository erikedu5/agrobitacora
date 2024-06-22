package com.meztlitech.agrobitacora.dto.filters;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ApplicationFilter extends PageFilter {

    public ApplicationFilter(int page, int size) {
        super.setPage(page);
        super.setSize(size);
    }
}
