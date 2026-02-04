package com.optiroute.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optiroute.dto.RouteOption;
import com.optiroute.dto.RouteSuggestionResponse;
import com.optiroute.model.DirectRoute;
import com.optiroute.repository.DirectRouteRepository;
import com.optiroute.repository.LocationRepository;

@Service
public class RouteService {

        @Autowired
        private DirectRouteRepository directRouteRepository;

        @Autowired
        private com.optiroute.repository.RouteStopRepository routeStopRepository;

        @Autowired
        private LocationService locationService;

        @org.springframework.cache.annotation.Cacheable(value = "routes", key = "#fromCity.toLowerCase() + '-' + #toCity.toLowerCase() + '-' + #timeWeight + '-' + #costWeight")
        public RouteSuggestionResponse getSuggestions(String fromCity, String toCity, Double timeWeight,
                        Double costWeight) {

                var fromLocation = locationService.findByNameIgnoreCase(fromCity)
                                .orElseThrow(() -> new RuntimeException("From Location not found: " + fromCity));

                var toLocation = locationService.findByNameIgnoreCase(toCity)
                                .orElseThrow(() -> new RuntimeException("To Location not found: " + toCity));

                // 1. Find routes covering this segment
                // Strategy: Find all stops for 'from', all stops for 'to', intersect on routeId
                // and check order
                List<com.optiroute.model.RouteStop> startStops = routeStopRepository
                                .findByLocationId(fromLocation.getId());
                List<com.optiroute.model.RouteStop> endStops = routeStopRepository.findByLocationId(toLocation.getId());

                List<DirectRoute> potentialRoutes = new java.util.ArrayList<>();

                // Check direct routes (legacy check ensuring backward compatibility if stops
                // not populated)
                potentialRoutes.addAll(directRouteRepository.findByFromLocationAndToLocation(fromLocation, toLocation));

                // Check segmented routes
                for (com.optiroute.model.RouteStop start : startStops) {
                        for (com.optiroute.model.RouteStop end : endStops) {
                                if (start.getDirectRoute().getId().equals(end.getDirectRoute().getId())) {
                                        if (start.getStopOrder() < end.getStopOrder()) {
                                                potentialRoutes.add(start.getDirectRoute());
                                        }
                                }
                        }
                }

                // Remove duplicates
                potentialRoutes = potentialRoutes.stream().distinct().collect(Collectors.toList());

                if (potentialRoutes.isEmpty()) {
                        // Keep empty list or try fallback logic if needed
                }

                List<RouteOption> options = potentialRoutes.stream()
                                .map(route -> mapToOption(route, fromLocation, toLocation, timeWeight, costWeight))
                                .collect(Collectors.toList());

                if (options.isEmpty()) {
                        return RouteSuggestionResponse.builder()
                                        .bestRoute(null)
                                        .otherRoutes(List.of())
                                        .build();
                }

                // Normalize and Calculate Efficiency Score
                normalizeAndScore(options, timeWeight, costWeight);

                // Sort by efficiency (Lowest score is best)
                options.sort(Comparator.comparingDouble(RouteOption::getEfficiencyScore));

                RouteOption bestRoute = options.get(0);
                List<RouteOption> otherRoutes = options.stream().skip(1).collect(Collectors.toList());

                // Sort other routes by cost
                otherRoutes.sort(Comparator.comparingDouble(RouteOption::getCost));

                return RouteSuggestionResponse.builder()
                                .bestRoute(bestRoute)
                                .otherRoutes(otherRoutes)
                                .build();
        }

        private RouteOption mapToOption(DirectRoute route, com.optiroute.model.Location from,
                        com.optiroute.model.Location to, Double timeWeight, Double costWeight) {

                // Default full route values
                double finalCost = route.getCost();
                int finalDuration = route.getDurationMinutes();

                // If stops are present, calculate segment specific cost/duration
                if (!route.getStops().isEmpty()) {
                        com.optiroute.model.RouteStop startStop = route.getStops().stream()
                                        .filter(s -> s.getLocation().getId().equals(from.getId())).findFirst()
                                        .orElse(null);
                        com.optiroute.model.RouteStop endStop = route.getStops().stream()
                                        .filter(s -> s.getLocation().getId().equals(to.getId())).findFirst()
                                        .orElse(null);

                        if (startStop != null && endStop != null) {
                                // Simplistic linear interpolation model for now
                                // In real world, use distFromStartKm difference
                                // Cost = (Segments Traveled / Total Stops) * Total Cost ?
                                // Or better: (endStopOrder - startStopOrder) / (MaxOrder)

                                int segmentsTraveled = endStop.getStopOrder() - startStop.getStopOrder();
                                int totalSegments = route.getStops().size() - 1; // n stops = n-1 segments
                                if (totalSegments > 0) {
                                        double ratio = (double) segmentsTraveled / totalSegments;
                                        finalCost = route.getCost() * ratio;
                                        finalDuration = (int) (route.getDurationMinutes() * ratio);
                                }
                        }
                }

                // Note: Score will be calculated later after normalization
                return RouteOption.builder()
                                .routeId(route.getId())
                                .transportType(route.getTransportType().name())
                                .durationMinutes(finalDuration)
                                .cost(finalCost)
                                .efficiencyScore(0.0) // Placeholder
                                .operator(route.getOperator())
                                .build();
        }

        private void normalizeAndScore(List<RouteOption> options, Double timeWeight, Double costWeight) {
                if (options.isEmpty())
                        return;

                double minCost = options.stream().mapToDouble(RouteOption::getCost).min().orElse(1.0);
                double maxCost = options.stream().mapToDouble(RouteOption::getCost).max().orElse(1.0);
                double minTime = options.stream().mapToDouble(RouteOption::getDurationMinutes).min().orElse(1.0);
                double maxTime = options.stream().mapToDouble(RouteOption::getDurationMinutes).max().orElse(1.0);

                if (maxCost == minCost)
                        maxCost = minCost + 1; // Avoid divide by zero
                if (maxTime == minTime)
                        maxTime = minTime + 1;

                for (RouteOption opt : options) {
                        // Normalize (0 to 1, where 0 is best)
                        // Normalized Cost = (Cost - Min) / (Max - Min)
                        double normCost = (opt.getCost() - minCost) / (maxCost - minCost);

                        // Normalized Time = (Time - Min) / (Max - Min)
                        double normTime = (opt.getDurationMinutes() - minTime) / (maxTime - minTime);

                        // Efficiency = Weighted Sum (Lower is better)
                        double score = (timeWeight * normTime) + (costWeight * normCost);
                        opt.setEfficiencyScore(score);
                }
        }
}