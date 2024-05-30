package com.meztlitech.agrobitacora.repository;

import com.meztlitech.agrobitacora.entity.CropEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CropRepository extends JpaRepository<CropEntity, Long> {

    @Query("FROM CropEntity c WHERE c.id = :cropId and c.user.id = :userId")
    Optional<CropEntity> findByIdAndUserId(Long cropId, Long userId);
    @Query("FROM CropEntity c WHERE c.user.id = :userId")
    Page<CropEntity> findAllByUserId(Long userId, Pageable paging);
}
