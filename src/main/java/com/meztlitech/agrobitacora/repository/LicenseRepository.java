package com.meztlitech.agrobitacora.repository;

import com.meztlitech.agrobitacora.entity.License;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LicenseRepository  extends JpaRepository<License, Long> {
}
