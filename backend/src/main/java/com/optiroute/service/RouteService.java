package com.optiroute.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import com.optiroute.dto.RouteOption;
import com.optiroute.dto.RouteSuggestionResponse;
import com.optiroute.model.DirectRoute;
import com.optiroute.repository.DirectRouteRepository;
import com.optiroute.model.RouteStop;

@Service
public class RouteService {

        @Autowired
        private DirectRouteRepository directRouteRepository;

        @Autowired
        private com.optiroute.repository.RouteStopRepository routeStopRepository;

        @Autowired
        private LocationService locationService;

        @Cacheable(value = "routes", key = "#fromCity.toLowerCase() + '-' + #toCity.toLowerCase() + '-' + #timeWeight + '-' + #costWeight")
        public RouteSuggestionResponse getSuggestions(String fromCity, String toCity, Double timeWeight,
                        Double costWeight) {

                var fromLocation = locationService.findByNameIgnoreCase(fromCity)
                                .orElseThrow(() -> new RuntimeException("From Location not found: " + fromCity));

                var toLocation = locationService.findByNameIgnoreCase(toCity)
                                .orElseThrow(() -> new RuntimeException("To Location not found: " + toCity));

                // 1. Find routes covering this segment
             
                List<com.optiroute.model.RouteStop> startStops = routeStopRepository
                                .findByLocationId(fromLocation.getId());
                List<com.optiroute.model.RouteStop> endStops = routeStopRepository.findByLocationId(toLocation.getId());

                List<DirectRoute> potentialRoutes = new java.util.ArrayList<>();

                // Check direct route
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


                RouteOption bestRoute=null;
                for(RouteOption option:options){
                        if(bestRoute==null){
                                bestRoute=option;
                        }else if(bestRoute.getEfficiencyScore()<option.getEfficiencyScore()){
                                bestRoute=option;
                        }
                }
                
                options.remove(bestRoute);
               options.sort(new Comparator<RouteOption>() {
                         @Override
                        public int compare(RouteOption a, RouteOption b) {
                                return a.getDurationMinutes() - b.getDurationMinutes();
                                                                        }
                                                        });

                List<RouteOption> otherRoutes = options;


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

                // If stops are present
                if (!route.getStops().isEmpty()) {
                        RouteStop startStop = null;
                        RouteStop endStop = null;

                        for (RouteStop stop : route.getStops()) {
                        if (stop.getLocation().getId().equals(from.getId())) {
                                startStop = stop;
                        } else if (stop.getLocation().getId().equals(to.getId())) {
                                endStop = stop;
                        }

                        // Early exit if both found
                        if (startStop != null && endStop != null) {
                                break;
                        }
                        }


                        if (startStop != null && endStop != null) {
                               
                                // Cost = (Segments Traveled / Total Stops) * Total Cost 
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

                double minCost = Double.MAX_VALUE;
                double maxCost = Double.MIN_VALUE;
                double minTime = Double.MAX_VALUE;
                double maxTime = Double.MIN_VALUE;

                for (RouteOption option : options) {
                double cost = option.getCost();
                double time = option.getDurationMinutes();

                if (cost < minCost) {
                        minCost = cost;
                }
                if (cost > maxCost) {
                        maxCost = cost;
                }

                if (time < minTime) {
                        minTime = time;
                }
                if (time > maxTime) {
                        maxTime = time;
                }
                }

                if (maxCost == minCost)
                        maxCost = minCost + 1; // Avoid divide by zero
                if (maxTime == minTime)
                        maxTime = minTime + 1;

                for (RouteOption opt : options) {
                        
                        // Normalized Cost  & time
                        double normCost = (opt.getCost() - minCost) / (maxCost - minCost);

                       
                        double normTime = (opt.getDurationMinutes() - minTime) / (maxTime - minTime);

                        // Efficiency 
                        double score = (timeWeight * normTime) + (costWeight * normCost);
                        opt.setEfficiencyScore(score);
                }
        }
}