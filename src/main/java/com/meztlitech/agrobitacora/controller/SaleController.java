package com.meztlitech.agrobitacora.controller;

import com.meztlitech.agrobitacora.dto.SaleDto;
import com.meztlitech.agrobitacora.dto.SaleSummaryResponse;
import com.meztlitech.agrobitacora.entity.SaleEntity;
import com.meztlitech.agrobitacora.service.SaleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;

@RestController
@RequestMapping("/sale")
@RequiredArgsConstructor
@Log4j2
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    public ResponseEntity<SaleEntity> create(@Valid @RequestBody SaleDto saleDto,
                                             @RequestHeader(value = "cropId") final Long cropId,
                                             @RequestHeader(value = "Authorization") final String token) {
        return ResponseEntity.ok(saleService.create(saleDto, cropId, token));
    }

    @PostMapping(consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<SaleEntity> createForm(@Valid SaleDto saleDto,
                                                 @RequestHeader(value = "cropId") final Long cropId,
                                                 @RequestHeader(value = "Authorization") final String token) {
        return ResponseEntity.ok(saleService.create(saleDto, cropId, token));
    }

    @GetMapping("/summary")
    public ResponseEntity<SaleSummaryResponse> summaryByDate(@RequestParam String date,
                                                             @RequestHeader(value = "cropId") final Long cropId,
                                                             @RequestHeader(value = "Authorization") final String token) {
        LocalDate d = LocalDate.parse(date);
        return ResponseEntity.ok(saleService.summaryByDate(d, cropId, token));
    }
}
