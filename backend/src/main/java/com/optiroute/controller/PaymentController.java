package com.optiroute.controller;

import com.optiroute.dto.PaymentInitiationRequest;
import com.optiroute.dto.PaymentRedirectResponse;
import com.optiroute.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/initiate")
    public ResponseEntity<PaymentRedirectResponse> initiatePayment(@RequestBody PaymentInitiationRequest request) {
        return ResponseEntity.ok(paymentService.initiatePayment(request.getRouteId()));
    }
}
