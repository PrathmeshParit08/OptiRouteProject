package com.optiroute.service;

import com.optiroute.dto.RouteOption;
import com.optiroute.dto.RouteSuggestionResponse;
import com.optiroute.model.DirectRoute;
import com.optiroute.repository.MockRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final MockRouteRepository routeRepository;

    public RouteSuggestionResponse suggestRoutes(String from, String to) {
        List<DirectRoute> routes = routeRepository.findRoutes(from, to);

        if (routes.isEmpty()) {
            return new RouteSuggestionResponse(null, new ArrayList<>());
        }

        List<RouteOption> options = routes.stream()
                .map(this::mapToOption)
                .sorted(Comparator.comparingDouble(RouteOption::getEfficiencyScore))
                .collect(Collectors.toList());

        // Best route is the one with the lowest efficiency score
        RouteOption bestRoute = options.get(0);

        // Remove best route from list for "otherRoutes"
        List<RouteOption> otherRoutes = new ArrayList<>(options);
        otherRoutes.remove(0);

        // Sort other routes by cost ascending
        otherRoutes.sort(Comparator.comparingDouble(RouteOption::getCost));

        return new RouteSuggestionResponse(bestRoute, otherRoutes);
    }

    private RouteOption mapToOption(DirectRoute route) {
        double efficiencyScore = (route.getDurationMinutes() * 0.6) + (route.getCost() * 0.4);
        return new RouteOption(
                route.getTransportType(),
                route.getDurationMinutes(),
                route.getCost(),
                efficiencyScore);
    }
}
