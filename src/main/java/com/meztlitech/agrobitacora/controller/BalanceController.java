package com.meztlitech.agrobitacora.controller;

import com.meztlitech.agrobitacora.dto.BalanceResponse;
import com.meztlitech.agrobitacora.service.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/balance")
@RequiredArgsConstructor
@Log4j2
public class BalanceController {

    private final BalanceService balanceService;

    @GetMapping("/general")
    public ResponseEntity<BalanceResponse> generalBalance(@RequestParam String start,
                                                          @RequestParam String end,
                                                          @RequestHeader(value = "cropId") Long cropId,
                                                          @RequestHeader(value = "Authorization") String token) {
        LocalDate s = LocalDate.parse(start);
        LocalDate e = LocalDate.parse(end);
        return ResponseEntity.ok(balanceService.getBalance(s, e, cropId, token));
    }
}
