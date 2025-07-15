package com.meztlitech.agrobitacora.controller;

import com.meztlitech.agrobitacora.entity.UserEntity;
import com.meztlitech.agrobitacora.service.EngineerService;
import com.meztlitech.agrobitacora.service.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/producer")
@RequiredArgsConstructor
@Log4j2
public class ProducerController {

    private final EngineerService engineerService;
    private final JwtService jwtService;
    private static final String ROLE_PRODUCTOR = "Productor";

    private Long validateProducer(String token) {
        Claims claims = jwtService.decodeToken(token);
        Object roleObj = claims.get("role");
        String role = null;
        if (roleObj instanceof Map) {
            Object name = ((Map<?,?>) roleObj).get("name");
            if (name != null) role = name.toString();
        } else if (roleObj != null) {
            role = roleObj.toString();
        }
        if (!ROLE_PRODUCTOR.equals(role)) {
            throw new HttpServerErrorException(HttpStatus.FORBIDDEN, "Access denied");
        }
        Object id = claims.get("id");
        return Long.parseLong(id.toString());
    }

    @GetMapping("/engineers")
    public ResponseEntity<List<UserEntity>> engineers(@RequestHeader("Authorization") String token) {
        Long producerId = validateProducer(token);
        return ResponseEntity.ok(engineerService.getEngineers(producerId));
    }

    @GetMapping("/engineers/all")
    public ResponseEntity<List<UserEntity>> allEngineers(@RequestHeader("Authorization") String token) {
        validateProducer(token);
        return ResponseEntity.ok(engineerService.getAllEngineers());
    }

    @PostMapping("/engineers")
    public ResponseEntity<Void> setEngineers(@RequestBody List<Long> engineerIds,
                                             @RequestHeader("Authorization") String token) {
        Long producerId = validateProducer(token);
        engineerService.setProducerEngineers(producerId, engineerIds);
        return ResponseEntity.ok().build();
    }
}
