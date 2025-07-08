package com.meztlitech.agrobitacora.repository;

import com.meztlitech.agrobitacora.entity.ApplicationDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import java.util.List;

@Repository
public interface ApplicationDetailRepository extends JpaRepository<ApplicationDetailEntity, Long> {

    @Query("FROM ApplicationDetailEntity ad WHERE ad.application.id = :applicationId")
    List<ApplicationDetailEntity> findAllByApplicationId(Long applicationId);

    Optional<ApplicationDetailEntity> findFirstByProductNameContainingIgnoreCase(String productName);
}
