package com.meztlitech.agrobitacora.service;

import com.meztlitech.agrobitacora.entity.StoreEntity;
import com.meztlitech.agrobitacora.dto.OrderItem;
import com.meztlitech.agrobitacora.entity.UserEntity;
import com.meztlitech.agrobitacora.repository.StoreRepository;
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
    private final StoreRepository storeRepository;

    @Value("${stores.url:http://localhost:8000/api/sucursales}")
    private String storesUrl;

    @Value("${stores.api-key:}")
    private String apiKey;

    @Value("${stores.order-url:http://localhost:8000/api/pedidos}")
    private String orderUrl;

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

    private Map<String, Object> getStoreInfo(Long storeId) {
        List<?> stores = getStores();
        for (Object o : stores) {
            if (o instanceof Map<?, ?> map) {
                Object vid = map.get("id");
                if (vid == null) vid = map.get("ID");
                if (vid == null) vid = map.get("codigo");
                if (vid != null && Long.parseLong(String.valueOf(vid)) == storeId) {
                    //noinspection unchecked
                    return (Map<String, Object>) map;
                }
            }
        }
        return null;
    }

    public void selectStore(Long storeId, String token) {
        Claims claims = jwtService.decodeToken(token);
        Long userId = Long.parseLong(claims.get("id").toString());
        UserEntity user = userRepository.findById(userId).orElseThrow();

        user.setBranchId(storeId);
        userRepository.save(user);

        Map<String, Object> info = getStoreInfo(storeId);
        if (info != null) {
            StoreEntity store = storeRepository.findById(storeId)
                    .orElse(StoreEntity.builder().id(storeId).build());
            store.setName(String.valueOf(info.getOrDefault("nombre", info.get("name"))));
            store.setPhone(String.valueOf(info.getOrDefault("whatsapp", info.get("telefono"))));
            storeRepository.save(store);
        }
    }

    public List<?> searchProducts(Long storeId, String name) {
        try {
            HttpHeaders headers = new HttpHeaders();
            if (apiKey != null && !apiKey.isBlank()) {
                headers.set("X-API-KEY", apiKey);
            }
            String url = storesUrl + "/" + storeId + "/productos?busqueda=" + name;
            ResponseEntity<List> res = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    List.class);
            return res.getBody();
        } catch (Exception e) {
            log.error("Error searching products", e);
            return Collections.emptyList();
        }
    }

    public void placeOrder(String token, List<OrderItem> items) {
        try {
            Claims claims = jwtService.decodeToken(token);
            Long userId = Long.parseLong(claims.get("id").toString());
            UserEntity user = userRepository.findById(userId).orElseThrow();
            if (user.getBranchId() == null) return;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            if (apiKey != null && !apiKey.isBlank()) {
                headers.set("X-API-KEY", apiKey);
            }

            Map<String, Object> body = Map.of(
                    "sucursal_id", user.getBranchId(),
                    "productos", items,
                    "nombre_solicitante", user.getName(),
                    "numero_solicitante", user.getWhatsapp()
            );

            restTemplate.postForEntity(orderUrl, new HttpEntity<>(body, headers), String.class);
        } catch (Exception e) {
            log.error("Error placing order", e);
        }
    }
}
