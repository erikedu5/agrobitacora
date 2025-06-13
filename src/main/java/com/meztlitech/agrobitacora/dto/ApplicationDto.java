package com.meztlitech.agrobitacora.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;

@Data
public class ApplicationDto {

    @NotBlank
    private String applicationType;

    @NotNull
    private LocalDateTime visitDate;

    @NotNull
    private LocalDateTime applicationDate;

    @NotBlank
    private String detail;

    @NotEmpty
    private List<ApplicationDetailDto> appDetails;

}
