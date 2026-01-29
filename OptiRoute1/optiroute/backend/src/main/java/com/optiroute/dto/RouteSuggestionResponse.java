package com.optiroute.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class RouteSuggestionResponse {
    private RouteOption bestRoute;
    private List<RouteOption> otherRoutes;
}
