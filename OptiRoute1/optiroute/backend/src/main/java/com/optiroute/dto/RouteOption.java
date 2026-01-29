package com.optiroute.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RouteOption {
    private Long routeId;
    private String transportType;
    private Integer durationMinutes;
    private Double cost;
    private Double efficiencyScore;
}
