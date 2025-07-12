package com.meztlitech.agrobitacora.repository;

import com.meztlitech.agrobitacora.entity.LaborEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LaborRepository extends JpaRepository<LaborEntity, Long> {

    @Query("FROM LaborEntity ie WHERE ie.crop.id = :cropId")
    Page<LaborEntity> findAllByCropId(Long cropId, Pageable paging);

    @Query("FROM LaborEntity ie WHERE ie.crop.id = :cropId")
    java.util.List<LaborEntity> findAllListByCropId(Long cropId);
}
