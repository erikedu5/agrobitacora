package com.meztlitech.agrobitacora.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignInRequest {
    // login can be either email or whatsapp number
    private String login;
    private String password;
    private boolean remember;
}
