package com.meztlitech.agrobitacora.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tienda")
@Data
public class StoreEntity {

    @Id
    private Long id; // external store ID

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private String phone;
}
