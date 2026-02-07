package com.optiroute.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.optiroute.dto.RouteOption;
import com.optiroute.dto.RouteSuggestionResponse;
import com.optiroute.model.DirectRoute;
import com.optiroute.model.Location;
import com.optiroute.model.RouteStop;
import com.optiroute.repository.DirectRouteRepository;
import com.optiroute.repository.RouteStopRepository;

@Service
public class RouteService {

    @Autowired
    private DirectRouteRepository directRouteRepository;

    @Autowired
    private RouteStopRepository routeStopRepository;

    @Autowired
    private LocationService locationService;

    @Cacheable(
        value = "routes",
        key = "#fromCity.toLowerCase() + '-' + #toCity.toLowerCase() + '-' + #timeWeight + '-' + #costWeight"
    )
    public RouteSuggestionResponse getSuggestions(
            String fromCity,
            String toCity,
            Double timeWeight,
            Double costWeight) {

        Location fromLocation = locationService.findByNameIgnoreCase(fromCity)
                .orElseThrow(() -> new RuntimeException("From location not found: " + fromCity));

        Location toLocation = locationService.findByNameIgnoreCase(toCity)
                .orElseThrow(() -> new RuntimeException("To location not found: " + toCity));

        List<RouteOption> options = new ArrayList<>();

        // 🔹 Fetch all routes (traditional approach)
        List<DirectRoute> allRoutes = directRouteRepository.findAll();

        for (DirectRoute route : allRoutes) {

            RouteStop startStop = null;
            RouteStop endStop = null;

            // Identify stops inside the SAME route
            for (RouteStop stop : route.getStops()) {
                if (stop.getLocation().getId().equals(fromLocation.getId())) {
                    startStop = stop;
                }
                if (stop.getLocation().getId().equals(toLocation.getId())) {
                    endStop = stop;
                }
            }

            // Route qualifies only if it covers the segment in order
            if (startStop != null && endStop != null &&
                    startStop.getStopOrder() < endStop.getStopOrder()) {

                options.add(mapToOption(route, startStop, endStop));
            }
        }

        if (options.isEmpty()) {
            return RouteSuggestionResponse.builder()
                    .bestRoute(null)
                    .otherRoutes(List.of())
                    .build();
        }

        // 🔹 Normalize and score
        normalizeAndScore(options, timeWeight, costWeight);

        // 🔹 Pick best route
        RouteOption bestRoute = options.stream()
                .max(Comparator.comparingDouble(RouteOption::getEfficiencyScore))
                .orElse(null);

        options.remove(bestRoute);

        // 🔹 Sort remaining routes (classic & predictable)
        options.sort(
                Comparator.comparingInt(RouteOption::getDurationMinutes)
                          .thenComparingDouble(RouteOption::getCost)
        );

        return RouteSuggestionResponse.builder()
                .bestRoute(bestRoute)
                .otherRoutes(options)
                .build();
    }

    // 🔹 Convert a route segment into an option
    private RouteOption mapToOption(
            DirectRoute route,
            RouteStop startStop,
            RouteStop endStop) {

        double finalCost = route.getCost();
        int finalDuration = route.getDurationMinutes();

        int segmentsTraveled = endStop.getStopOrder() - startStop.getStopOrder();
        int totalSegments = route.getStops().size() - 1;

        if (totalSegments > 0) {
            double ratio = (double) segmentsTraveled / totalSegments;
            finalCost = route.getCost() * ratio;
            finalDuration = (int) (route.getDurationMinutes() * ratio);
        }

        return RouteOption.builder()
                .routeId(route.getId())
                .transportType(route.getTransportType().name())
                .operator(route.getOperator())
                .cost(finalCost)
                .durationMinutes(finalDuration)
                .efficiencyScore(0.0)
                .build();
    }

    // 🔹 Traditional min–max normalization with weighted score
    private void normalizeAndScore(
            List<RouteOption> options,
            Double timeWeight,
            Double costWeight) {

        double minCost = options.stream().mapToDouble(RouteOption::getCost).min().orElse(0);
        double maxCost = options.stream().mapToDouble(RouteOption::getCost).max().orElse(1);
        double minTime = options.stream().mapToDouble(RouteOption::getDurationMinutes).min().orElse(0);
        double maxTime = options.stream().mapToDouble(RouteOption::getDurationMinutes).max().orElse(1);

        if (maxCost == minCost) maxCost++;
        if (maxTime == minTime) maxTime++;

        for (RouteOption opt : options) {
            double normCost = (opt.getCost() - minCost) / (maxCost - minCost);
            double normTime = (opt.getDurationMinutes() - minTime) / (maxTime - minTime);

            double score = (timeWeight * normTime) + (costWeight * normCost);
            opt.setEfficiencyScore(score);
        }
    }
}
