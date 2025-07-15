package com.meztlitech.agrobitacora.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "engineer_producers",
       uniqueConstraints = @UniqueConstraint(columnNames = {"engineer_id", "producer_id"}))
@Data
public class EngineerProducerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "engineer_id", nullable = false)
    private UserEntity engineer;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "producer_id", nullable = false)
    private UserEntity producer;
}
