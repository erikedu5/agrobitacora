package com.meztlitech.agrobitacora.dto;

public record EncryptionResult(byte[] cipherText, byte[] key, byte[] iv) { }

