package com.meztlitech.agrobitacora.repository;

import com.meztlitech.agrobitacora.dto.enums.ApplicationType;
import com.meztlitech.agrobitacora.entity.ApplicationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationEntity, Long> {

    @Query("FROM ApplicationEntity a WHERE a.applicationType = :applicationType AND a.crop.id = :cropId")
    Page<ApplicationEntity> findAllByApplicationType(ApplicationType applicationType, Long cropId, Pageable paging);
}
