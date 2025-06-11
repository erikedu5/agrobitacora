package com.meztlitech.agrobitacora.controller;

import com.meztlitech.agrobitacora.dto.ProductionDto;
import com.meztlitech.agrobitacora.dto.ProductionResponse;
import com.meztlitech.agrobitacora.dto.filters.ProductionFilter;
import com.meztlitech.agrobitacora.entity.ProductionEntity;
import com.meztlitech.agrobitacora.service.ProductionService;
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
import jakarta.validation.Valid;

@RestController
@RequestMapping("/production")
@RequiredArgsConstructor
@Log4j2
public class ProductionController {

    private final ProductionService productionService;

    @PostMapping
    public ResponseEntity<ProductionEntity> create(@Valid @RequestBody ProductionDto productionDto,
                                                      @RequestHeader(value = "cropId") final Long cropId,
                                                      @RequestHeader(value = "Authorization") final String token) {
        return ResponseEntity.ok(productionService.create(productionDto, cropId, token));
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<ProductionEntity> createForm(@Valid ProductionDto productionDto,
                                                       @RequestHeader(value = "cropId") final Long cropId,
                                                       @RequestHeader(value = "Authorization") final String token) {
        return ResponseEntity.ok(productionService.create(productionDto, cropId, token));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<ProductionResponse>> getAll(@RequestParam int page,
                                                           @RequestParam int size,
                                                           @RequestHeader(value = "cropId") final Long cropId,
                                                           @RequestHeader(value = "Authorization") final String token) {
        ProductionFilter productionFilter = new ProductionFilter(page, size);
        return ResponseEntity.ok(productionService.getAll(productionFilter, cropId, token));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductionResponse> findById(@PathVariable(name = "id") final long id) {
        return ResponseEntity.ok(productionService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductionEntity> update(@PathVariable Long id,
                                                   @Valid @RequestBody ProductionDto productionDto,
                                                   @RequestHeader(value = "Authorization") final String token) {
        return ResponseEntity.ok(productionService.update(id, productionDto, token));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @RequestHeader(value = "Authorization") final String token) {
        productionService.delete(id, token);
        return ResponseEntity.ok().build();
    }
}
