package com.meztlitech.agrobitacora.service;

import com.meztlitech.agrobitacora.dto.BillDto;
import com.meztlitech.agrobitacora.dto.BillResponse;
import com.meztlitech.agrobitacora.dto.BillSummaryResponse;
import com.meztlitech.agrobitacora.dto.filters.BillFilter;
import com.meztlitech.agrobitacora.entity.BillEntity;
import com.meztlitech.agrobitacora.repository.BillRepository;
import com.meztlitech.agrobitacora.repository.CropRepository;
import com.meztlitech.agrobitacora.util.CropUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class BillService {

    private final CropUtil cropUtil;
    private final CropRepository cropRepository;
    private final BillRepository billRepository;

    private final Path root = Paths.get("uploads");

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    public BillEntity createBill(BillDto billDto, Long cropId, String token) {
        cropUtil.validateCropByUser(token, cropId);
        BillEntity billEntity = new BillEntity();
        billEntity.setCrop(cropRepository.findById(cropId).orElseThrow());
        billEntity.setBillDate(billDto.getBillDate());
        billEntity.setCost(billDto.getCost());
        billEntity.setConcept(billDto.getConcept());
        billEntity.setIdBillAssociated(billDto.getIdBillAssociated());
        billEntity.setKindBillAssociated(billDto.getKindBillAssociated());
        return billRepository.save(billEntity);
    }

    public BillEntity uploadFile(Long id, MultipartFile file, String token) {
        BillEntity bill = billRepository.findById(id).orElseThrow();
        Path destination = this.root.resolve(file.getOriginalFilename());
        try {
            Files.copy(file.getInputStream(), destination);
        } catch (Exception e) {
            if (e instanceof FileAlreadyExistsException) {
                throw new RuntimeException("A file of that name already exists.");
            }

            throw new RuntimeException(e.getMessage());
        }
        bill.setPathEvidence(destination.toString());
        return billRepository.save(bill);
    }

    public BillEntity updateBill(Long id, BillDto billDto, String token) {
        BillEntity bill = billRepository.findById(id).orElseThrow();
        cropUtil.validateCropByUser(token, bill.getCrop().getId());
        bill.setBillDate(billDto.getBillDate());
        bill.setCost(billDto.getCost());
        bill.setConcept(billDto.getConcept());
        bill.setIdBillAssociated(billDto.getIdBillAssociated());
        bill.setKindBillAssociated(billDto.getKindBillAssociated());
        return billRepository.save(bill);
    }

    public void delete(Long id, String token) {
        BillEntity bill = billRepository.findById(id).orElseThrow();
        cropUtil.validateCropByUser(token, bill.getCrop().getId());
        billRepository.delete(bill);
    }

    public Page<BillResponse> getAll(BillFilter billFilter, Long cropId, String token) {
        cropUtil.validateCropByUser(token, cropId);
        Pageable paging = PageRequest.of(billFilter.getPage(), billFilter.getSize());
        Page<BillEntity> bills = billRepository.findAllByCropId(cropId, paging);
        List<BillResponse> content = new ArrayList<>();
        bills.get().forEach(b -> content.add(this.mapBill(b)));
        return new PageImpl<>(content, bills.getPageable(), bills.getTotalElements());
    }

    public BillSummaryResponse summaryByRange(LocalDate start, LocalDate end, Long cropId, String token) {
        cropUtil.validateCropByUser(token, cropId);
        List<BillEntity> bills = billRepository.findAllByDateRange(cropId, start, end);
        BillSummaryResponse res = new BillSummaryResponse();
        res.setStartDate(start);
        res.setEndDate(end);
        List<BillResponse> responses = bills.stream().map(this::mapBill).collect(Collectors.toList());
        res.setBills(responses);
        double total = bills.stream().mapToDouble(BillEntity::getCost).sum();
        res.setTotal(total);
        return res;
    }

    private BillResponse mapBill(BillEntity bill) {
        BillResponse r = new BillResponse();
        r.setId(bill.getId());
        r.setBillDate(bill.getBillDate());
        r.setConcept(bill.getConcept());
        r.setCost(bill.getCost());
        r.setKindBillAssociated(bill.getKindBillAssociated());
        r.setIdBillAssociated(bill.getIdBillAssociated());
        return r;
    }

}
