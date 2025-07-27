package com.meztlitech.agrobitacora.controller;

import com.meztlitech.agrobitacora.dto.LicenseSchoolRequest;
import com.meztlitech.agrobitacora.dto.LicenseSchoolResponse;
import com.meztlitech.agrobitacora.service.LicenseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/license")
@RequiredArgsConstructor
@Log4j2
public class SchoolLicenceController {

    private final LicenseService licenseService;

    @PostMapping
    public ResponseEntity<LicenseSchoolResponse> generateLicense(@RequestBody LicenseSchoolRequest licenseRequest) {
        return ResponseEntity.ok(licenseService.generateLicense(licenseRequest));
    }
}
