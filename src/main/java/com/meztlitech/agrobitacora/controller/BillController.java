package com.meztlitech.agrobitacora.controller;

import com.meztlitech.agrobitacora.dto.BillDto;
import com.meztlitech.agrobitacora.dto.BillResponse;
import com.meztlitech.agrobitacora.dto.BillSummaryResponse;
import com.meztlitech.agrobitacora.dto.filters.BillFilter;
import com.meztlitech.agrobitacora.entity.BillEntity;
import com.meztlitech.agrobitacora.service.BillService;
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
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;
import java.time.LocalDate;

@RestController
@RequestMapping("/bill")
@RequiredArgsConstructor
@Log4j2
public class BillController {

    private final BillService billService;

    @PostMapping
    public ResponseEntity<BillEntity> create(@Valid @RequestBody BillDto billDto,
                                             @RequestHeader(value = "cropId") final Long cropId,
                                             @RequestHeader(value = "Authorization") final String token) {
        return ResponseEntity.ok(billService.createBill(billDto, cropId, token));
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<BillEntity> createForm(@Valid BillDto billDto,
                                                 @RequestHeader(value = "cropId") final Long cropId,
                                                 @RequestHeader(value = "Authorization") final String token) {
        return ResponseEntity.ok(billService.createBill(billDto, cropId, token));
    }

    @PostMapping("/file/{id}")
    public ResponseEntity<BillEntity> addFile(@PathVariable(name = "id") final long id,
                                              @RequestHeader(value = "Authorization") final String token,
                                              @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(billService.uploadFile(id, file, token));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<BillResponse>> getAll(@RequestParam int page,
                                                     @RequestParam int size,
                                                     @RequestHeader(value = "cropId") final Long cropId,
                                                     @RequestHeader(value = "Authorization") final String token) {
        BillFilter filter = new BillFilter(page, size);
        return ResponseEntity.ok(billService.getAll(filter, cropId, token));
    }

    @GetMapping("/summary")
    public ResponseEntity<BillSummaryResponse> summary(@RequestParam String start,
                                                       @RequestParam String end,
                                                       @RequestHeader(value = "cropId") final Long cropId,
                                                       @RequestHeader(value = "Authorization") final String token) {
        return ResponseEntity.ok(billService.summaryByRange(LocalDate.parse(start), LocalDate.parse(end), cropId, token));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BillEntity> update(@PathVariable Long id,
                                             @Valid @RequestBody BillDto billDto,
                                             @RequestHeader(value = "Authorization") final String token) {
        return ResponseEntity.ok(billService.updateBill(id, billDto, token));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @RequestHeader(value = "Authorization") final String token) {
        billService.delete(id, token);
        return ResponseEntity.ok().build();
    }

}
