package com.optiroute.controller;

import com.optiroute.dto.RouteRequest;
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

    @GetMapping("/suggest")
public ResponseEntity<?> suggestRoutes(@RequestParam String from, @RequestParam String to) {
    try {
        return ResponseEntity.ok(routeService.getSuggestions(from, to));
    } catch (RuntimeException e) {
        // This will confirm if the "403" is actually a "Location not found" error
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
}
