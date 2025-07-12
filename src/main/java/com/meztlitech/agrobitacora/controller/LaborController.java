package com.meztlitech.agrobitacora.controller;

import com.meztlitech.agrobitacora.dto.LaborDto;
import com.meztlitech.agrobitacora.dto.filters.LaborFilter;
import com.meztlitech.agrobitacora.entity.LaborEntity;
import com.meztlitech.agrobitacora.service.LaborService;
import com.meztlitech.agrobitacora.dto.CatalogDto;
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
@RequestMapping("/labor")
@RequiredArgsConstructor
@Log4j2
public class LaborController {

    private final LaborService laborService;

    @PostMapping
    public ResponseEntity<LaborEntity> create(@Valid @RequestBody LaborDto laborDto,
                                              @RequestHeader(value = "cropId") final Long cropId,
                                              @RequestHeader(value = "Authorization") final String token) {
        return ResponseEntity.ok(laborService.create(laborDto, cropId, token));
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<LaborEntity> createForm(@Valid LaborDto laborDto,
                                                  @RequestHeader(value = "cropId") final Long cropId,
                                                  @RequestHeader(value = "Authorization") final String token) {
        return ResponseEntity.ok(laborService.create(laborDto, cropId, token));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<LaborEntity>> getAll(@RequestParam int page,
                                                            @RequestParam int size,
                                                            @RequestHeader(value = "cropId") final Long cropId,
                                                            @RequestHeader(value = "Authorization") final String token) {
        LaborFilter laborFilter = new LaborFilter(page, size);
        return ResponseEntity.ok(laborService.getAll(laborFilter, cropId, token));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LaborEntity> update(@PathVariable Long id,
                                              @Valid @RequestBody LaborDto laborDto,
                                              @RequestHeader(value = "Authorization") final String token) {
        return ResponseEntity.ok(laborService.update(id, laborDto, token));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @RequestHeader(value = "Authorization") final String token) {
        laborService.delete(id, token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/catalog")
    public ResponseEntity<java.util.List<CatalogDto>> catalog(@RequestHeader(value = "cropId") final Long cropId,
                                                              @RequestHeader(value = "Authorization") final String token) {
        return ResponseEntity.ok(laborService.catalog(cropId, token));
    }
}
