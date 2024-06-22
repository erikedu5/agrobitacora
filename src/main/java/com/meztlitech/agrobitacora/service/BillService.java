package com.meztlitech.agrobitacora.service;

import com.meztlitech.agrobitacora.dto.BillDto;
import com.meztlitech.agrobitacora.entity.BillEntity;
import com.meztlitech.agrobitacora.repository.BillRepository;
import com.meztlitech.agrobitacora.repository.CropRepository;
import com.meztlitech.agrobitacora.util.CropUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

}
