package com.meztlitech.agrobitacora.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordRecoveryRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String whatsapp;

    @NotBlank
    private String password;
}
