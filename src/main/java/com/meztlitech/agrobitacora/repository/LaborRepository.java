package com.meztlitech.agrobitacora.repository;

import com.meztlitech.agrobitacora.entity.LaborEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LaborRepository extends JpaRepository<LaborEntity, Long> {

    @Query("FROM LaborRepository l WHERE l.crop.id = :cropId")
    Page<LaborEntity> findAllByCropId(Long cropId, Pageable paging);
}
