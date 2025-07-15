package com.meztlitech.agrobitacora.repository;

import com.meztlitech.agrobitacora.entity.EngineerReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EngineerReviewRepository extends JpaRepository<EngineerReviewEntity, Long> {

    @Query("SELECT AVG(r.rating) FROM EngineerReviewEntity r WHERE r.engineer.id = :engineerId")
    Double averageRatingByEngineerId(Long engineerId);

    Optional<EngineerReviewEntity> findByEngineerIdAndProducerId(Long engineerId, Long producerId);
}
