package com.optiroute.service;

import com.optiroute.dto.PaymentRedirectResponse;
import com.optiroute.model.DirectRoute;
import com.optiroute.repository.DirectRouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Autowired
    private DirectRouteRepository directRouteRepository;

    public PaymentRedirectResponse initiatePayment(Long routeId) {
        DirectRoute route = directRouteRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found"));

        return PaymentRedirectResponse.builder()
                .paymentUrl("/payment?routeId=" + routeId + "&amount=" + route.getCost())
                .routeId(routeId)
                .amount(route.getCost())
                .build();
    }
}
