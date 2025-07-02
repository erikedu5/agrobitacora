package com.meztlitech.agrobitacora.controller;

import com.meztlitech.agrobitacora.service.WhatsAppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/whatsapp")
@RequiredArgsConstructor
@Log4j2
public class WhatsAppController {

    private final WhatsAppService whatsAppService;

    @PostMapping("/send")
    public ResponseEntity<Void> send(@RequestParam String to, @RequestParam String message) {
        whatsAppService.sendMessage(to, message);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody Map<String, Object> payload) {
        log.info("Webhook payload received: {}", payload);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/webhook")
    public ResponseEntity<String> verify(@RequestParam("hub.challenge") String challenge) {
        return ResponseEntity.ok(challenge);
    }
}
