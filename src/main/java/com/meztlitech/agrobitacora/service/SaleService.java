package com.meztlitech.agrobitacora.service;

import com.meztlitech.agrobitacora.dto.SaleDto;
import com.meztlitech.agrobitacora.dto.SaleResponse;
import com.meztlitech.agrobitacora.dto.SaleSummaryResponse;
import com.meztlitech.agrobitacora.entity.SaleEntity;
import com.meztlitech.agrobitacora.repository.CropRepository;
import com.meztlitech.agrobitacora.repository.SaleRepository;
import com.meztlitech.agrobitacora.util.CropUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class SaleService {

    private final SaleRepository saleRepository;
    private final CropRepository cropRepository;
    private final CropUtil cropUtil;

    public SaleEntity create(SaleDto saleDto, Long cropId, String token) {
        cropUtil.validateCropByUser(token, cropId);
        SaleEntity sale = new SaleEntity();
        sale.setSaleDate(saleDto.getSaleDate());
        sale.setPackages(saleDto.getPackages());
        sale.setPrice(saleDto.getPrice());
        sale.setCrop(cropRepository.findById(cropId).orElseThrow());
        return saleRepository.save(sale);
    }

    public SaleSummaryResponse summaryByDate(LocalDate date, Long cropId, String token) {
        cropUtil.validateCropByUser(token, cropId);
        List<SaleEntity> sales = saleRepository.findAllByDate(cropId, date);
        SaleSummaryResponse res = new SaleSummaryResponse();
        res.setDate(date);
        List<SaleResponse> responses = sales.stream().map(this::mapSale).collect(Collectors.toList());
        res.setSales(responses);
        double total = sales.stream().mapToDouble(s -> s.getPrice() * s.getPackages()).sum();
        res.setTotal(total);
        return res;
    }

    private SaleResponse mapSale(SaleEntity sale) {
        SaleResponse r = new SaleResponse();
        r.setId(sale.getId());
        r.setSaleDate(sale.getSaleDate());
        r.setPackages(sale.getPackages());
        r.setPrice(sale.getPrice());
        return r;
    }
}
