package com.meztlitech.agrobitacora.service;

import com.meztlitech.agrobitacora.dto.WeatherRecordDto;
import com.meztlitech.agrobitacora.entity.CropEntity;
import com.meztlitech.agrobitacora.entity.WeatherRecordEntity;
import com.meztlitech.agrobitacora.repository.CropRepository;
import com.meztlitech.agrobitacora.repository.WeatherRecordRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class WeatherService {

    private final RestTemplate restTemplate;
    private final WeatherRecordRepository recordRepository;
    private final CropRepository cropRepository;

    public WeatherInfo getWeather(double latitude, double longitude, Long cropId) {
        String url = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude +
                "&longitude=" + longitude + "&current_weather=true" +
                "&hourly=relative_humidity_2m,soil_moisture_0_1cm,shortwave_radiation" +
                "&daily=temperature_2m_max,temperature_2m_min,precipitation_sum,et0_fao_evapotranspiration" +
                "&timezone=auto";
        try {
            Map<?, ?> resp = restTemplate.getForObject(url, Map.class);
            if (resp != null && resp.containsKey("current_weather")) {
                Object cwObj = resp.get("current_weather");
                if (cwObj instanceof Map<?, ?> cw) {
                    Double temp = toDouble(cw.get("temperature"));
                    Double wind = toDouble(cw.get("windspeed"));
                    WeatherInfo info = new WeatherInfo();
                    info.setTemperature(temp);
                    info.setWindspeed(wind);
                    Object hourlyObj = resp.get("hourly");
                    if (hourlyObj instanceof Map<?,?> ho) {
                        info.setRelativeHumidity(firstDouble(ho.get("relative_humidity_2m")));
                        info.setSoilHumidity(firstDouble(ho.get("soil_moisture_0_1cm")));
                        info.setSolarRadiation(firstDouble(ho.get("shortwave_radiation")));
                    }
                    Object dailyObj = resp.get("daily");
                    if (dailyObj instanceof Map<?,?> d) {
                        info.setTemperatureMax(firstDouble(d.get("temperature_2m_max")));
                        info.setTemperatureMin(firstDouble(d.get("temperature_2m_min")));
                        info.setPrecipitation(firstDouble(d.get("precipitation_sum")));
                        info.setEvapotranspiration(firstDouble(d.get("et0_fao_evapotranspiration")));
                    }
                    return info;
                }
            }
        } catch (Exception ex) {
            log.error("Error fetching weather", ex);
        }
        // fallback values when the API cannot be reached
        WeatherInfo info = new WeatherInfo();
        info.setTemperature(20.0);
        return info;
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

    private Double firstDouble(Object obj) {
        if (obj instanceof List<?> list && !list.isEmpty()) {
            return toDouble(list.get(0));
        }
        return null;
    }

    public WeatherRecordEntity saveRecord(Long cropId, WeatherRecordDto dto) {
        CropEntity crop = cropRepository.findById(cropId).orElseThrow();
        WeatherRecordEntity record = recordRepository
                .findTopByCropIdAndDate(cropId, dto.getDate())
                .orElse(new WeatherRecordEntity());
        record.setCrop(crop);
        record.setDate(dto.getDate());
        record.setEvapotranspiration(dto.getEvapotranspiration());
        record.setTemperatureMin(dto.getTemperatureMin());
        record.setTemperatureMax(dto.getTemperatureMax());
        record.setPrecipitation(dto.getPrecipitation());
        record.setWindSpeed(dto.getWindSpeed());
        record.setRelativeHumidity(dto.getRelativeHumidity());
        record.setSolarRadiation(dto.getSolarRadiation());
        record.setSoilHumidity(dto.getSoilHumidity());
        return recordRepository.save(record);
    }

    public java.util.List<WeatherRecordDto> getHistory(double latitude, double longitude) {
        String url = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude +
                "&longitude=" + longitude + "&past_days=7" +
                "&daily=temperature_2m_max,temperature_2m_min" +
                "&timezone=auto";
        try {
            Map<?, ?> resp = restTemplate.getForObject(url, Map.class);
            if (resp != null && resp.containsKey("daily")) {
                Object dailyObj = resp.get("daily");
                if (dailyObj instanceof Map<?,?> d) {
                    java.util.List<?> dates = (java.util.List<?>) d.get("time");
                    java.util.List<?> tmin = (java.util.List<?>) d.get("temperature_2m_min");
                    java.util.List<?> tmax = (java.util.List<?>) d.get("temperature_2m_max");
                    java.util.List<WeatherRecordDto> result = new java.util.ArrayList<>();
                    if (dates != null) {
                        for (int i = 0; i < dates.size(); i++) {
                            WeatherRecordDto dto = new WeatherRecordDto();
                            dto.setDate(java.time.LocalDate.parse(String.valueOf(dates.get(i))));
                            if (tmin != null && i < tmin.size()) dto.setTemperatureMin(toDouble(tmin.get(i)));
                            if (tmax != null && i < tmax.size()) dto.setTemperatureMax(toDouble(tmax.get(i)));
                            result.add(dto);
                        }
                    }
                    return result;
                }
            }
        } catch (Exception e) {
            log.error("Error fetching weather history", e);
        }
        return java.util.List.of();
    }

    public WeatherInfo mergeWithRecord(WeatherInfo info, Long cropId) {
        LocalDate today = LocalDate.now();
        recordRepository.findTopByCropIdAndDate(cropId, today).ifPresent(r -> {
            if (info.getEvapotranspiration() == null) info.setEvapotranspiration(r.getEvapotranspiration());
            if (info.getTemperatureMin() == null) info.setTemperatureMin(r.getTemperatureMin());
            if (info.getTemperatureMax() == null) info.setTemperatureMax(r.getTemperatureMax());
            if (info.getPrecipitation() == null) info.setPrecipitation(r.getPrecipitation());
            if (info.getWindspeed() == null) info.setWindspeed(r.getWindSpeed());
            if (info.getRelativeHumidity() == null) info.setRelativeHumidity(r.getRelativeHumidity());
            if (info.getSolarRadiation() == null) info.setSolarRadiation(r.getSolarRadiation());
            if (info.getSoilHumidity() == null) info.setSoilHumidity(r.getSoilHumidity());
        });
        return info;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeatherInfo {
        private Double temperature;
        private Double windspeed;
        private Double temperatureMin;
        private Double temperatureMax;
        private Double precipitation;
        private Double relativeHumidity;
        private Double solarRadiation;
        private Double evapotranspiration;
        private Double soilHumidity;
        private String location = "Unknown";
    }
}
