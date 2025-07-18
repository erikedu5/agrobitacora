package com.meztlitech.agrobitacora.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignInRequest {
    // login must be a valid email
    @NotBlank
    @Email
    private String login;

    @NotBlank
    private String password;
    private boolean remember;
}
