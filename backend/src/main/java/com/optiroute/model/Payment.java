package com.optiroute.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long routeId;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
}
