package com.meztlitech.agrobitacora.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class WeatherService {

    private final RestTemplate restTemplate;

    public WeatherInfo getWeather(double latitude, double longitude) {
        String url = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude +
                "&longitude=" + longitude + "&current_weather=true";
        try {
            Map<?, ?> resp = restTemplate.getForObject(url, Map.class);
            if (resp != null && resp.containsKey("current_weather")) {
                Object cwObj = resp.get("current_weather");
                if (cwObj instanceof Map<?, ?> cw) {
                    Double temp = toDouble(cw.get("temperature"));
                    Double wind = toDouble(cw.get("windspeed"));
                    return new WeatherInfo(temp, wind);
                }
            }
        } catch (Exception ex) {
            log.error("Error fetching weather", ex);
        }
        return null;
    }

    private Double toDouble(Object obj) {
        if (obj instanceof Number n) {
            return n.doubleValue();
        }
        try {
            return Double.parseDouble(obj.toString());
        } catch (Exception e) {
            return null;
        }
    }

    @Data
    @AllArgsConstructor
    public static class WeatherInfo {
        private Double temperature;
        private Double windspeed;
    }
}
