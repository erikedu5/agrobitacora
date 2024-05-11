package com.meztlitech.agrobitacora.controller;

import com.meztlitech.agrobitacora.dto.CropDto;
import com.meztlitech.agrobitacora.dto.filters.CropFilter;
import com.meztlitech.agrobitacora.entity.CropEntity;
import com.meztlitech.agrobitacora.service.CropService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/crop")
@RequiredArgsConstructor
@Log4j2
public class CropController {

    private final CropService cropService;

    @PostMapping
    public ResponseEntity<CropEntity> create(@RequestBody CropDto cropDto,
                                             @RequestHeader(value = "Authorization") final String token) {
        return ResponseEntity.ok(cropService.create(cropDto, token));
    }

    @PostMapping("/all")
    public ResponseEntity<Page<CropEntity>> getAll(@RequestBody CropFilter cropFilter,
                                       @RequestHeader(value = "Authorization") final String token) {
        return ResponseEntity.ok(cropService.getAll(cropFilter, token));
    }
}
