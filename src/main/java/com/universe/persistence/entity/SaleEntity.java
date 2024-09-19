package com.universe.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sales")
public class SaleEntity {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID id;

    private LocalDate date;

    @Column(name = "total_price")
    private double totalPrice;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL)
    private List<SaleDetailEntity> saleDetailList;
}
