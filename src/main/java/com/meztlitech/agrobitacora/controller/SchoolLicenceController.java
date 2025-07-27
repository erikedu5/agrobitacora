package com.meztlitech.agrobitacora.controller;

import com.meztlitech.agrobitacora.dto.LicenseSchoolRequest;
import com.meztlitech.agrobitacora.dto.LicenseSchoolResponse;
import com.meztlitech.agrobitacora.entity.License;
import com.meztlitech.agrobitacora.service.LicenseService;
import com.meztlitech.agrobitacora.service.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/license")
@RequiredArgsConstructor
@Log4j2
public class SchoolLicenceController {

    private final LicenseService licenseService;
    private final JwtService jwtService;
    private static final String ROLE_ADMIN = "Admin";

    private void validateAdmin(String token) {
        Claims claims = jwtService.decodeToken(token);
        Object roleObj = claims.get("role");
        String role = null;
        if (roleObj instanceof java.util.Map<?,?> map) {
            Object name = map.get("name");
            if (name != null) role = name.toString();
        } else if (roleObj != null) {
            role = roleObj.toString();
        }
        if (!ROLE_ADMIN.equals(role)) {
            throw new org.springframework.web.client.HttpServerErrorException(
                    org.springframework.http.HttpStatus.FORBIDDEN, "Access denied");
        }
    }

    @PostMapping
    public ResponseEntity<LicenseSchoolResponse> generateLicense(@RequestBody LicenseSchoolRequest licenseRequest) {
        return ResponseEntity.ok(licenseService.generateLicense(licenseRequest));
    }

    @GetMapping("/all")
    public ResponseEntity<java.util.List<License>> all(@RequestHeader("Authorization") String token) {
        validateAdmin(token);
        return ResponseEntity.ok(licenseService.findAll());
    }
}
