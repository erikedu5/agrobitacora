package com.meztlitech.agrobitacora.controller;

import com.meztlitech.agrobitacora.entity.CropEntity;
import com.meztlitech.agrobitacora.repository.CropRepository;
import com.meztlitech.agrobitacora.service.WeatherService;
import com.meztlitech.agrobitacora.util.CropUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
                crop.getLatitud(), crop.getLongitud());
        if (info == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(info);
    }
}
