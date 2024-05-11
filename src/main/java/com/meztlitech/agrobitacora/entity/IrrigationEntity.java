package com.meztlitech.agrobitacora.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meztlitech.agrobitacora.dto.enums.IrrigationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "irrigations")
@Data
public class IrrigationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "type")
    private IrrigationType type;

    @Column(name = "timeInIrrigationInMinutes")
    private Long timeInIrrigationInMinutes;

    @Column(name = "conditions")
    private String[] conditions;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "crop_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private CropEntity crop;

}
