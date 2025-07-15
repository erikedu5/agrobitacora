package com.meztlitech.agrobitacora.repository;

import com.meztlitech.agrobitacora.entity.EngineerProducerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EngineerProducerRepository extends JpaRepository<EngineerProducerEntity, Long> {
    List<EngineerProducerEntity> findByEngineerId(Long engineerId);
    List<EngineerProducerEntity> findByProducerId(Long producerId);
    boolean existsByEngineerIdAndProducerId(Long engineerId, Long producerId);
}
