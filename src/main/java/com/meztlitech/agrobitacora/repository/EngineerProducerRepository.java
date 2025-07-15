package com.meztlitech.agrobitacora.repository;

import com.meztlitech.agrobitacora.entity.EngineerProducerEntity;
import com.meztlitech.agrobitacora.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EngineerProducerRepository extends JpaRepository<EngineerProducerEntity, Long> {

    @Query("SELECT ep.producer FROM EngineerProducerEntity ep WHERE ep.engineer.id = :engineerId")
    List<UserEntity> findProducersByEngineerId(Long engineerId);

    @Query("SELECT ep.engineer FROM EngineerProducerEntity ep WHERE ep.producer.id = :producerId")
    List<UserEntity> findEngineersByProducerId(Long producerId);

    void deleteByEngineerIdAndProducerId(Long engineerId, Long producerId);

    boolean existsByEngineerIdAndProducerId(Long engineerId, Long producerId);
}
