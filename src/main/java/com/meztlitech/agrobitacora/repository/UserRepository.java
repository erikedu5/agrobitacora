package com.meztlitech.agrobitacora.repository;

import com.meztlitech.agrobitacora.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("FROM UserEntity u WHERE u.userName = :username")
    Optional<UserEntity> findByUserName(String username);
}
