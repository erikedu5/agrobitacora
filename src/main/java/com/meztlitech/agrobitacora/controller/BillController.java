package com.meztlitech.agrobitacora.controller;

import com.meztlitech.agrobitacora.dto.BillDto;
import com.meztlitech.agrobitacora.entity.BillEntity;
import com.meztlitech.agrobitacora.service.BillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/bill")
@RequiredArgsConstructor
@Log4j2
public class BillController {

    private final BillService billService;

    @PostMapping
    public ResponseEntity<BillEntity> create(@RequestBody BillDto billDto,
                                             @RequestHeader(value = "cropId") final Long cropId,
                                             @RequestHeader(value = "Authorization") final String token) {
        return ResponseEntity.ok(billService.createBill(billDto, cropId, token));
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<BillEntity> createForm(BillDto billDto,
                                                 @RequestHeader(value = "cropId") final Long cropId,
                                                 @RequestHeader(value = "Authorization") final String token) {
        return ResponseEntity.ok(billService.createBill(billDto, cropId, token));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BillEntity> addFile(@PathVariable(name = "id") final long id,
                                              @RequestHeader(value = "Authorization") final String token,
                                              @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(billService.uploadFile(id, file, token));
    }

}
