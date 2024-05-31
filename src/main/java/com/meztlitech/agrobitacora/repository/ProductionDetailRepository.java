package com.meztlitech.agrobitacora.repository;

import com.meztlitech.agrobitacora.entity.ProductionDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductionDetailRepository extends JpaRepository<ProductionDetailEntity, Long> {

    @Query("FROM ProductionDetailEntity pde WHERE pde.production.id = :id")
    List<ProductionDetailEntity> findAllByProductionId(Long id);
}
