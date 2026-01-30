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
    private LocationRepository locationRepository;

    public RouteSuggestionResponse getSuggestions(String fromCity, String toCity) {

        var fromLocation = locationRepository.findByNameIgnoreCase(fromCity)
                .orElseThrow(() -> new RuntimeException("From Location not found: " + fromCity));

        var toLocation = locationRepository.findByNameIgnoreCase(toCity)
                .orElseThrow(() -> new RuntimeException("To Location not found: " + toCity));

        List<DirectRoute> routes =
                directRouteRepository.findByFromLocationAndToLocation(fromLocation, toLocation);

        if (routes.isEmpty()) {
            return RouteSuggestionResponse.builder()
                    .bestRoute(null)
                    .otherRoutes(List.of())
                    .build();
        }

        List<RouteOption> options = routes.stream()
                .map(this::mapToOption)
                .collect(Collectors.toList());

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

    private RouteOption mapToOption(DirectRoute route) {
        double efficiencyScore =
                (route.getDurationMinutes() * 0.6) + (route.getCost() * 0.4);

        return RouteOption.builder()
                .routeId(route.getId())
                .transportType(route.getTransportType().name())
                .durationMinutes(route.getDurationMinutes())
                .cost(route.getCost())
                .efficiencyScore(efficiencyScore)
                .build();
    }
}