package com.meztlitech.agrobitacora.controller;

import com.meztlitech.agrobitacora.dto.IrrigationDto;
import com.meztlitech.agrobitacora.dto.filters.IrrigationFilter;
import com.meztlitech.agrobitacora.entity.IrrigationEntity;
import com.meztlitech.agrobitacora.service.IrrigationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/irrigation")
@RequiredArgsConstructor
@Log4j2
public class IrrigationController {

    private final IrrigationService irrigationService;

    @GetMapping("/all")
    public ResponseEntity<Page<IrrigationEntity>> getAll(@RequestParam int page,
                                                         @RequestParam int size,
                                                         @RequestHeader(value = "cropId") final Long cropId,
                                                         @RequestHeader(value = "Authorization") final String token) {
        IrrigationFilter irrigationFilter = new IrrigationFilter(page, size);
        return ResponseEntity.ok(irrigationService.getAll(irrigationFilter, cropId, token));
    }

    @PostMapping
    public ResponseEntity<IrrigationEntity> create(@RequestBody IrrigationDto irrigationDto,
                                 @RequestHeader(value = "cropId") final Long cropId,
                                 @RequestHeader(value = "Authorization") final String token) {
        return ResponseEntity.ok(irrigationService.create(irrigationDto, cropId, token));
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<IrrigationEntity> createForm(IrrigationDto irrigationDto,
                                   @RequestHeader(value = "cropId") final Long cropId,
                                   @RequestHeader(value = "Authorization") final String token) {
        return ResponseEntity.ok(irrigationService.create(irrigationDto, cropId, token));
    }
}
