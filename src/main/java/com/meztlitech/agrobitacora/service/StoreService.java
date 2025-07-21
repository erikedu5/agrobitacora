package com.meztlitech.agrobitacora.service;

import com.meztlitech.agrobitacora.entity.UserEntity;
import com.meztlitech.agrobitacora.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class StoreService {

    private final RestTemplate restTemplate;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Value("${stores.url:http://localhost:8000/api/sucursales}")
    private String storesUrl;

    @Value("${stores.api-key:}")
    private String apiKey;

    public List<?> getStores() {
        try {
            HttpHeaders headers = new HttpHeaders();
            if (apiKey != null && !apiKey.isBlank()) {
                headers.set("X-API-KEY", apiKey);
            }
            ResponseEntity<List> res = restTemplate.exchange(
                    storesUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    List.class);
            return res.getBody();
        } catch (Exception e) {
            log.error("Error fetching stores", e);
            return Collections.emptyList();
        }
    }

    public void selectStore(Long storeId, String token) {
        Claims claims = jwtService.decodeToken(token);
        Long userId = Long.parseLong(claims.get("id").toString());
        UserEntity user = userRepository.findById(userId).orElseThrow();
        user.setBranchId(storeId);
        userRepository.save(user);
    }
}
