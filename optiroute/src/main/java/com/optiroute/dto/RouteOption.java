package com.optiroute.dto;

import com.optiroute.model.DirectRoute;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteOption {
    private DirectRoute.TransportType transportType;
    private int durationMinutes;
    private double cost;
    private double efficiencyScore;
}
