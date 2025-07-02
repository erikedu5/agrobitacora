package com.meztlitech.agrobitacora.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class WhatsAppService {

    private final RestTemplate restTemplate;

    @Value("${whatsapp.token:}")
    private String token;

    @Value("${whatsapp.phone-number-id:}")
    private String phoneNumberId;

    private static final String GRAPH_URL = "https://graph.facebook.com/v18.0/";

    public void sendMessage(String to, String message) {
        if (token == null || token.isBlank() || phoneNumberId == null || phoneNumberId.isBlank()) {
            log.warn("WhatsApp credentials are not configured");
            return;
        }
        String url = GRAPH_URL + phoneNumberId + "/messages";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        Map<String, Object> text = new HashMap<>();
        text.put("body", message);
        Map<String, Object> body = new HashMap<>();
        body.put("messaging_product", "whatsapp");
        body.put("to", to);
        body.put("type", "text");
        body.put("text", text);

        try {
            restTemplate.postForEntity(url, new HttpEntity<>(body, headers), String.class);
        } catch (Exception ex) {
            log.error("Error sending WhatsApp message", ex);
        }
    }
}
