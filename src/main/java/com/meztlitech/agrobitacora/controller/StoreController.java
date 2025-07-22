package com.meztlitech.agrobitacora.controller;

import com.meztlitech.agrobitacora.dto.OrderRequest;
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

    @GetMapping("/{id}/products")
    public ResponseEntity<List<?>> products(@PathVariable Long id,
                                            @RequestParam(name = "q") String name) {
        return ResponseEntity.ok((List<?>) storeService.searchProducts(id, name));
    }

    @PostMapping("/order")
    public ResponseEntity<Void> order(@RequestBody OrderRequest request,
                                      @RequestHeader("Authorization") String token) {
        storeService.placeOrder(token, request.getItems());
        return ResponseEntity.ok().build();
    }
}
