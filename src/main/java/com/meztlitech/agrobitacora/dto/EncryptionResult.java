package com.meztlitech.agrobitacora.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EncryptionResult {
    byte[] cipherText;
    byte[] key;
    byte[] iv;
}

