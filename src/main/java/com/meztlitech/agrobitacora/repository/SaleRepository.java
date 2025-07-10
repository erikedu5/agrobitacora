package com.meztlitech.agrobitacora.repository;

import com.meztlitech.agrobitacora.entity.SaleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<SaleEntity, Long> {

    @Query("FROM SaleEntity s WHERE s.crop.id = :cropId AND DATE(s.saleDate) = :date")
    List<SaleEntity> findAllByDate(Long cropId, LocalDate date);
}
