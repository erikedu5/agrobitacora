package com.meztlitech.agrobitacora.repository;

import com.meztlitech.agrobitacora.entity.IrrigationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IrrigationRepository extends JpaRepository<IrrigationEntity, Long> {

    @Query("FROM IrrigationEntity ie WHERE ie.crop.id = :cropId")
    Page<IrrigationEntity> findAllByCropId(Long cropId, Pageable paging);

    @Query("FROM IrrigationEntity ie WHERE ie.crop.id = :cropId")
    java.util.List<IrrigationEntity> findAllListByCropId(Long cropId);
}
