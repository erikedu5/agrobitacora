package com.meztlitech.agrobitacora.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class WeatherRecordDto {
    @NotNull
    private LocalDate date;
    private Double evapotranspiration;
    private Double temperatureMin;
    private Double temperatureMax;
    private Double precipitation;
    private Double windSpeed;
    private Double relativeHumidity;
    private Double solarRadiation;
    private Double soilHumidity;
}
