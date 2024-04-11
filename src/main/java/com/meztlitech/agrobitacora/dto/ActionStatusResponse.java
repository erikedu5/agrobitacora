package com.meztlitech.agrobitacora.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Data
public class ActionStatusResponse {

    public Long id;
    public HttpStatus status;
    public String description;
    public Map<HttpStatus, String> errors;

}
