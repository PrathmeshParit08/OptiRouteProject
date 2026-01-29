package com.optiroute.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentRedirectResponse {
    private String paymentUrl;
    private Long routeId;
    private Double amount;
}
