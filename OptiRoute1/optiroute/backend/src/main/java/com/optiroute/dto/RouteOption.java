package com.optiroute.dto;

public class RouteOption {
    private Long routeId;
    private String transportType;
    private Integer durationMinutes;
    private Double cost;
    private Double efficiencyScore;

    // --- Constructors ---
    public RouteOption() {}

    public RouteOption(Long routeId, String transportType, Integer durationMinutes, Double cost, Double efficiencyScore) {
        this.routeId = routeId;
        this.transportType = transportType;
        this.durationMinutes = durationMinutes;
        this.cost = cost;
        this.efficiencyScore = efficiencyScore;
    }

    // --- Getters ---
    public Long getRouteId() { return routeId; }
    public String getTransportType() { return transportType; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public Double getCost() { return cost; }
    public Double getEfficiencyScore() { return efficiencyScore; }

    // --- Setters ---
    public void setRouteId(Long routeId) { this.routeId = routeId; }
    public void setTransportType(String transportType) { this.transportType = transportType; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    public void setCost(Double cost) { this.cost = cost; }
    public void setEfficiencyScore(Double efficiencyScore) { this.efficiencyScore = efficiencyScore; }

    // --- Manual Builder Implementation ---
    public static RouteOptionBuilder builder() {
        return new RouteOptionBuilder();
    }

    public static class RouteOptionBuilder {
        private Long routeId;
        private String transportType;
        private Integer durationMinutes;
        private Double cost;
        private Double efficiencyScore;

        public RouteOptionBuilder routeId(Long routeId) {
            this.routeId = routeId;
            return this;
        }

        public RouteOptionBuilder transportType(String transportType) {
            this.transportType = transportType;
            return this;
        }

        public RouteOptionBuilder durationMinutes(Integer durationMinutes) {
            this.durationMinutes = durationMinutes;
            return this;
        }

        public RouteOptionBuilder cost(Double cost) {
            this.cost = cost;
            return this;
        }

        public RouteOptionBuilder efficiencyScore(Double efficiencyScore) {
            this.efficiencyScore = efficiencyScore;
            return this;
        }

        public RouteOption build() {
            return new RouteOption(routeId, transportType, durationMinutes, cost, efficiencyScore);
        }
    }
}