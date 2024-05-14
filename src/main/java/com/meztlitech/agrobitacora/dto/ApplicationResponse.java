package com.meztlitech.agrobitacora.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ApplicationResponse {

    private String applicationType;

    private LocalDateTime visitDate;

    private LocalDateTime applicationDate;

    private String detail;

    private List<ApplicationDetailDto> appDetails;

    private String technicalName;

    private String recommendationType;

    private Long id;

}
