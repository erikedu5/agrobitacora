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

    @Query("FROM UserEntity u WHERE u.userName = :login OR u.whatsapp = :login")
    Optional<UserEntity> findByUserNameOrWhatsapp(String login);

    @Query("FROM UserEntity u WHERE u.role.name = :roleName AND u.active = true")
    java.util.List<UserEntity> findByRoleName(String roleName);

    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.role.name = :roleName AND u.active = true")
    long countByRoleName(String roleName);
}
