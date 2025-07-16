package com.meztlitech.agrobitacora.controller;

import com.meztlitech.agrobitacora.dto.WeatherRecordDto;
import com.meztlitech.agrobitacora.entity.CropEntity;
import com.meztlitech.agrobitacora.entity.WeatherRecordEntity;
import com.meztlitech.agrobitacora.repository.CropRepository;
import com.meztlitech.agrobitacora.service.WeatherService;
import com.meztlitech.agrobitacora.util.CropUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;
    private final CropRepository cropRepository;
    private final CropUtil cropUtil;

    @GetMapping
    public ResponseEntity<WeatherService.WeatherInfo> getWeather(
            @RequestHeader(value = "cropId") Long cropId,
            @RequestHeader(value = "Authorization") String token) {
        cropUtil.validateCropByUser(token, cropId);
        CropEntity crop = cropRepository.findById(cropId).orElseThrow();
        WeatherService.WeatherInfo info = weatherService.getWeather(
                crop.getLatitud(), crop.getLongitud(), cropId);
        if (info == null) {
            return ResponseEntity.noContent().build();
        }
        info.setLocation(crop.getLocation());
        weatherService.mergeWithRecord(info, cropId);
        return ResponseEntity.ok(info);
    }

    @PostMapping
    public ResponseEntity<WeatherRecordEntity> saveRecord(
            @Valid @RequestBody WeatherRecordDto dto,
            @RequestHeader("cropId") Long cropId,
            @RequestHeader("Authorization") String token) {
        cropUtil.validateCropByUser(token, cropId);
        WeatherRecordEntity saved = weatherService.saveRecord(cropId, dto);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/history")
    public ResponseEntity<java.util.List<WeatherRecordEntity>> history(
            @RequestHeader("cropId") Long cropId,
            @RequestHeader("Authorization") String token) {
        cropUtil.validateCropByUser(token, cropId);
        return ResponseEntity.ok(weatherService.getHistory(cropId));
    }
}
