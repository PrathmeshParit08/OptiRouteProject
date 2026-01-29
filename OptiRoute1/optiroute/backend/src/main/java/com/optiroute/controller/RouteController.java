package com.optiroute.controller;

import com.optiroute.dto.RouteSuggestionResponse;
import com.optiroute.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/routes")
@CrossOrigin(origins = "*")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @PostMapping("/suggest")
    public ResponseEntity<RouteSuggestionResponse> suggestRoutes(@RequestParam String from, @RequestParam String to) {
        return ResponseEntity.ok(routeService.getSuggestions(from, to));
    }
}
