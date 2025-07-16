package com.meztlitech.agrobitacora.repository;

import com.meztlitech.agrobitacora.entity.WeatherRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface WeatherRecordRepository extends JpaRepository<WeatherRecordEntity, Long> {
    Optional<WeatherRecordEntity> findTopByCropIdAndDate(Long cropId, LocalDate date);

    java.util.List<WeatherRecordEntity> findTop7ByCropIdOrderByDateDesc(Long cropId);
}
