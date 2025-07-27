package com.meztlitech.agrobitacora.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "licensesSchool", uniqueConstraints= @UniqueConstraint(columnNames = {"schoolName", "expirationDate"}))
@Data
public class License {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String cipherText;
    private String key;
    private String iv;
    private String schoolName;
    private String studentTotal;
    private String decryptedText;
    private String expirationDate;

}
