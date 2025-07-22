package com.meztlitech.agrobitacora.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OrderItem {
    @JsonProperty("producto_id")
    private Long productoId;
    private Integer cantidad;
}
