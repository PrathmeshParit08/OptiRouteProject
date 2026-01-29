package com.optiroute.repository;

import com.optiroute.model.DirectRoute;
import com.optiroute.model.DirectRoute.TransportType;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class MockRouteRepository {
    private final List<DirectRoute> routes = new ArrayList<>();

    public MockRouteRepository() {
        // Mock Data: Mumbai -> Jaipur
        routes.add(new DirectRoute("1", "Mumbai", "Jaipur", TransportType.FLIGHT, 90, 3500));
        routes.add(new DirectRoute("2", "Mumbai", "Jaipur", TransportType.TRAIN, 600, 900));
        routes.add(new DirectRoute("3", "Mumbai", "Jaipur", TransportType.BUS, 780, 700));

        // Mock Data: Delhi -> Bangalore
        routes.add(new DirectRoute("4", "Delhi", "Bangalore", TransportType.FLIGHT, 150, 5000));
        routes.add(new DirectRoute("5", "Delhi", "Bangalore", TransportType.TRAIN, 2100, 1500));

        // Mock Data: New York -> London
        routes.add(new DirectRoute("6", "New York", "London", TransportType.FLIGHT, 420, 45000));
    }

    public List<DirectRoute> findRoutes(String from, String to) {
        return routes.stream()
                .filter(route -> route.getFromLocation().equalsIgnoreCase(from)
                        && route.getToLocation().equalsIgnoreCase(to))
                .collect(Collectors.toList());
    }
}
