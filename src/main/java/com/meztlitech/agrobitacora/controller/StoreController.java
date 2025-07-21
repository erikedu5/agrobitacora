package com.meztlitech.agrobitacora.controller;

import com.meztlitech.agrobitacora.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
@Log4j2
public class StoreController {

    private final StoreService storeService;

    @GetMapping("/all")
    public ResponseEntity<List<?>> all() {
        return ResponseEntity.ok((List<?>) storeService.getStores());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> select(@PathVariable Long id,
                                       @RequestHeader("Authorization") String token) {
        storeService.selectStore(id, token);
        return ResponseEntity.ok().build();
    }
}
