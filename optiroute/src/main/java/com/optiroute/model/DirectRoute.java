package com.optiroute.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DirectRoute {
    private String id;
    private String fromLocation;
    private String toLocation;
    private TransportType transportType;
    private int durationMinutes;
    private double cost;

    public enum TransportType {
        FLIGHT, TRAIN, BUS
    }
}
