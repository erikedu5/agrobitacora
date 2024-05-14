package com.meztlitech.agrobitacora.controller;

import com.meztlitech.agrobitacora.dto.ApplicationDto;
import com.meztlitech.agrobitacora.dto.ApplicationResponse;
import com.meztlitech.agrobitacora.dto.filters.ApplicationFilter;
import com.meztlitech.agrobitacora.service.FumigationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fumigation")
@RequiredArgsConstructor
@Log4j2
public class FumigationController {

    private final FumigationService fumigationService;

    @PostMapping
    public ResponseEntity<ApplicationResponse> create(@RequestBody ApplicationDto applicationDto,
                                                      @RequestHeader(value = "cropId") final Long cropId,
                                                      @RequestHeader(value = "Authorization") final String token) {
        return ResponseEntity.ok(fumigationService.create(applicationDto, cropId, token));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<ApplicationResponse>> getAll(@RequestParam int page,
                                                            @RequestParam int size,
                                                            @RequestHeader(value = "cropId") final Long cropId,
                                                            @RequestHeader(value = "Authorization") final String token) {
        ApplicationFilter applicationFilter = new ApplicationFilter(page, size);
        return ResponseEntity.ok(fumigationService.getAll(applicationFilter, cropId, token));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> findById(@PathVariable(name = "id") final long id) {
        return ResponseEntity.ok(fumigationService.findById(id));
    }
}
