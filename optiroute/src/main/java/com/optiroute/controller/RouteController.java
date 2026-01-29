package com.optiroute.controller;

import com.optiroute.dto.RouteRequest;
import com.optiroute.dto.RouteSuggestionResponse;
import com.optiroute.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @PostMapping("/suggest")
    public ResponseEntity<RouteSuggestionResponse> suggestRoutes(@RequestBody RouteRequest request) {
        // Validation could be added here (e.g. check if from/to are empty)

        RouteSuggestionResponse response = routeService.suggestRoutes(request.getFrom(), request.getTo());
        return ResponseEntity.ok(response);
    }
}
