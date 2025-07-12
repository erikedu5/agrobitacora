package com.meztlitech.agrobitacora.service;

import com.meztlitech.agrobitacora.dto.BalanceResponse;
import com.meztlitech.agrobitacora.entity.BillEntity;
import com.meztlitech.agrobitacora.entity.SaleEntity;
import com.meztlitech.agrobitacora.repository.BillRepository;
import com.meztlitech.agrobitacora.repository.SaleRepository;
import com.meztlitech.agrobitacora.util.CropUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class BalanceService {

    private final CropUtil cropUtil;
    private final BillRepository billRepository;
    private final SaleRepository saleRepository;

    public BalanceResponse getBalance(LocalDate start, LocalDate end, Long cropId, String token) {
        cropUtil.validateCropByUser(token, cropId);
        List<BillEntity> bills = billRepository.findAllByDateRange(cropId, start, end);
        List<SaleEntity> sales = saleRepository.findAllByDateRange(cropId, start, end);

        double billsTotal = bills.stream().mapToDouble(BillEntity::getCost).sum();
        double salesTotal = sales.stream().mapToDouble(s -> s.getPrice() * s.getPackages()).sum();

        BalanceResponse res = new BalanceResponse();
        res.setStartDate(start);
        res.setEndDate(end);
        res.setBillsTotal(billsTotal);
        res.setSalesTotal(salesTotal);
        res.setBalance(salesTotal - billsTotal);
        return res;
    }
}
