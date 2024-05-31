package com.meztlitech.agrobitacora.repository;

import com.meztlitech.agrobitacora.entity.ProductionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionRepository extends JpaRepository<ProductionEntity, Long> {

    @Query("FROM ProductionEntity pe WHERE pe.crop.id = :cropId")
    Page<ProductionEntity> findAllByCropId(Long cropId, Pageable paging);
}
