package com.meztlitech.agrobitacora.dto;

import com.meztlitech.agrobitacora.entity.RoleEntity;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserResponse {

    private String fullName;
    private RoleEntity role;
    private String email;
    private String whatsapp;
    private Long id;
    private String token;
    private Integer maxCrops;
    private Long cropCount;
    private Long branchId;
}
