package com.meztlitech.agrobitacora.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private List<OrderItem> items;
}
