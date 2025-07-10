package com.meztlitech.agrobitacora.repository;

import com.meztlitech.agrobitacora.entity.BillEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<BillEntity, Long> {

    @Query("FROM BillEntity b WHERE b.crop.id = :cropId")
    Page<BillEntity> findAllByCropId(Long cropId, Pageable paging);

    @Query("FROM BillEntity b WHERE b.crop.id = :cropId AND DATE(b.billDate) BETWEEN :start AND :end")
    List<BillEntity> findAllByDateRange(Long cropId, LocalDate start, LocalDate end);
}
