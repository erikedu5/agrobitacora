package com.meztlitech.agrobitacora.controller;

import com.meztlitech.agrobitacora.dto.filters.CropFilter;
import com.meztlitech.agrobitacora.entity.CropEntity;
import com.meztlitech.agrobitacora.entity.RoleEntity;
import com.meztlitech.agrobitacora.entity.UserEntity;
import com.meztlitech.agrobitacora.service.EngineerService;
import com.meztlitech.agrobitacora.service.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/engineer")
@RequiredArgsConstructor
@Log4j2
public class EngineerController {

    private final EngineerService engineerService;
    private final JwtService jwtService;
    private static final String ROLE_INGENIERO = "Ingeniero";

    private Long validateEngineer(String token) {
        Claims claims = jwtService.decodeToken(token);
        Object roleObj = claims.get("role");
        String role = null;
        if (roleObj instanceof Map) {
            Object name = ((Map<?,?>) roleObj).get("name");
            if (name != null) role = name.toString();
        } else if (roleObj != null) {
            role = roleObj.toString();
        }
        if (!ROLE_INGENIERO.equals(role)) {
            throw new HttpServerErrorException(HttpStatus.FORBIDDEN, "Access denied");
        }
        Object id = claims.get("id");
        return Long.parseLong(id.toString());
    }

    @GetMapping("/producers")
    public ResponseEntity<List<UserEntity>> producers(@RequestHeader("Authorization") String token) {
        Long engineerId = validateEngineer(token);
        return ResponseEntity.ok(engineerService.getProducers(engineerId));
    }

    @GetMapping("/producers/all")
    public ResponseEntity<List<UserEntity>> allProducers(@RequestHeader("Authorization") String token) {
        validateEngineer(token);
        return ResponseEntity.ok(engineerService.getAllProducers());
    }

    @PostMapping("/producers")
    public ResponseEntity<Void> setProducers(@RequestBody List<Long> producerIds,
                                             @RequestHeader("Authorization") String token) {
        Long engineerId = validateEngineer(token);
        engineerService.setEngineerProducers(engineerId, producerIds);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/crops")
    public ResponseEntity<Page<CropEntity>> crops(@RequestParam Long producerId,
                                                  @RequestParam int page,
                                                  @RequestParam int size,
                                                  @RequestHeader("Authorization") String token) {
        validateEngineer(token);
        CropFilter filter = new CropFilter(page, size);
        return ResponseEntity.ok(engineerService.getCrops(producerId, filter));
    }
}
