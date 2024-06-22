package com.meztlitech.agrobitacora.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meztlitech.agrobitacora.dto.enums.KindBillAssociated;
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
@Table(name = "bill")
@Data
public class BillEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "bill_date")
    private LocalDateTime billDate;

    @Column(name = "concept")
    private String concept;

    @Column(name = "cost")
    private Double cost;

    @Column(name = "path_evidence")
    private String pathEvidence;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "crop_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private CropEntity crop;

    @Column(name = "bill_associated_id")
    private Long idBillAssociated;


    @Column(name = "kind_bill_associated")
    private KindBillAssociated kindBillAssociated;
}
