package com.meztlitech.agrobitacora.controller;

import com.meztlitech.agrobitacora.dto.CropDto;
import com.meztlitech.agrobitacora.dto.filters.CropFilter;
import com.meztlitech.agrobitacora.entity.CropEntity;
import com.meztlitech.agrobitacora.service.CropService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/crop")
@RequiredArgsConstructor
@Log4j2
public class CropController {

    private final CropService cropService;

    @GetMapping("/all")
    public ResponseEntity<Page<CropEntity>> getAll(@RequestParam int page,
                                                   @RequestParam int size,
                                                   @RequestHeader(value = "Authorization") final String token) {
        CropFilter cropFilter = new CropFilter(page, size);
        return ResponseEntity.ok(cropService.getAll(cropFilter, token));
    }

    @GetMapping("/getById")
    public ResponseEntity<CropEntity> getById(@RequestParam Long id, @RequestHeader(value = "Authorization") final String token){
        return ResponseEntity.ok(cropService.getById(id, token));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CropEntity> update(@Valid @RequestBody CropDto cropDto, @PathVariable Long id, @RequestHeader(value = "Authorization") final String token){
        return ResponseEntity.ok(cropService.update(cropDto, id, token));
    }

    @PostMapping
    public ResponseEntity<CropEntity> create(@Valid @RequestBody CropDto cropDto,
                                             @RequestHeader(value = "Authorization") final String token) {
        return ResponseEntity.ok(cropService.create(cropDto, token));
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<CropEntity> createForm(@Valid CropDto cropDto,
                                                 @RequestHeader(value = "Authorization") final String token) {
        return ResponseEntity.ok(cropService.create(cropDto, token));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @RequestHeader(value = "Authorization") final String token) {
        cropService.delete(id, token);
        return ResponseEntity.ok().build();
    }

}
