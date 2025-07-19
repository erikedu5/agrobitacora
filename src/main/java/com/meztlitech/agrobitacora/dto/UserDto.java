package com.meztlitech.agrobitacora.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
@RequiredArgsConstructor
public class UserDto {

    private String name;
    private Long roleId;
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
    private String whatsapp;
    private Integer maxCrops;
}
