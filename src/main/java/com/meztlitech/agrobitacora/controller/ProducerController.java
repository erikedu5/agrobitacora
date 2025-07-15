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

    private void validateProducer(String token) {
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
    }

    @GetMapping("/engineers")
    public ResponseEntity<List<UserEntity>> engineers(@RequestHeader("Authorization") String token) {
        validateProducer(token);
        return ResponseEntity.ok(engineerService.getAllEngineers());
    }

    @GetMapping("/my-engineers")
    public ResponseEntity<List<UserEntity>> myEngineers(@RequestHeader("Authorization") String token) {
        validateProducer(token);
        return ResponseEntity.ok(engineerService.getEngineers(token));
    }

    @PostMapping("/engineers/{engineerId}")
    public ResponseEntity<Void> addEngineer(@PathVariable Long engineerId,
                                            @RequestHeader("Authorization") String token) {
        validateProducer(token);
        engineerService.addEngineer(token, engineerId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/engineers/{engineerId}")
    public ResponseEntity<Void> removeEngineer(@PathVariable Long engineerId,
                                               @RequestHeader("Authorization") String token) {
        validateProducer(token);
        engineerService.removeEngineer(token, engineerId);
        return ResponseEntity.ok().build();
    }
}
