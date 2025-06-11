package com.meztlitech.agrobitacora.controller;

import com.meztlitech.agrobitacora.dto.ApplicationDto;
import com.meztlitech.agrobitacora.dto.ApplicationResponse;
import com.meztlitech.agrobitacora.dto.filters.ApplicationFilter;
import com.meztlitech.agrobitacora.service.NutritionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/nutrition")
@RequiredArgsConstructor
@Log4j2
public class NutritionController {

    private final NutritionService nutritionService;

    @PostMapping
    public ResponseEntity<ApplicationResponse> create(@RequestBody ApplicationDto applicationDto,
                                                      @RequestHeader(value = "cropId") final Long cropId,
                                                      @RequestHeader(value = "Authorization") final String token) {
        return ResponseEntity.ok(nutritionService.create(applicationDto, cropId, token));
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<ApplicationResponse> createForm(ApplicationDto applicationDto,
                                                          @RequestHeader(value = "cropId") final Long cropId,
                                                          @RequestHeader(value = "Authorization") final String token) {
        return ResponseEntity.ok(nutritionService.create(applicationDto, cropId, token));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<ApplicationResponse>> getAll(@RequestParam int page,
                                                            @RequestParam int size,
                                                            @RequestHeader(value = "cropId") final Long cropId,
                                                            @RequestHeader(value = "Authorization") final String token) {
        ApplicationFilter applicationFilter = new ApplicationFilter(page, size);
        return ResponseEntity.ok(nutritionService.getAll(applicationFilter, cropId, token));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> findById(@PathVariable(name = "id") final long id) {
        return ResponseEntity.ok(nutritionService.findById(id));
    }
}
