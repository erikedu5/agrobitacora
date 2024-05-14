package com.meztlitech.agrobitacora.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserDto {

    private String name;
    private Long roleId;
    private String email;
    private String password;
}
