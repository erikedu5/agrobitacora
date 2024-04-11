package com.meztlitech.agrobitacora.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserDto {

    private String userName;
    private String password;
}
