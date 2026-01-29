package com.optiroute.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "direct_routes")
public class DirectRoute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_location_id", nullable = false)
    private Location fromLocation;

    @ManyToOne
    @JoinColumn(name = "to_location_id", nullable = false)
    private Location toLocation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransportType transportType;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Column(nullable = false)
    private Double cost;
}
