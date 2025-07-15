package com.meztlitech.agrobitacora.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "weather_records")
public class WeatherRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "record_date")
    private LocalDate date;

    @Column(name = "evapotranspiration")
    private Double evapotranspiration;

    @Column(name = "temp_min")
    private Double temperatureMin;

    @Column(name = "temp_max")
    private Double temperatureMax;

    @Column(name = "precipitation")
    private Double precipitation;

    @Column(name = "wind_speed")
    private Double windSpeed;

    @Column(name = "relative_humidity")
    private Double relativeHumidity;

    @Column(name = "solar_radiation")
    private Double solarRadiation;

    @Column(name = "soil_humidity")
    private Double soilHumidity;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "crop_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private CropEntity crop;
}
