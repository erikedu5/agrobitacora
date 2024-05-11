package com.meztlitech.agrobitacora.repository;

import com.meztlitech.agrobitacora.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {


    @Query("FROM RoleEntity u WHERE u.isDefault = :isDefault")
    RoleEntity findByIsDefault(boolean isDefault);
}
