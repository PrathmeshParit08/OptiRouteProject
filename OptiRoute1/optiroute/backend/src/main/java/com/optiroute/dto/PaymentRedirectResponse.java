package com.optiroute.dto;

public class PaymentRedirectResponse {
    private String paymentUrl;
    private Long routeId;
    private Double amount;

    // Default Constructor
    public PaymentRedirectResponse() {}

    // All-Args Constructor
    public PaymentRedirectResponse(String paymentUrl, Long routeId, Double amount) {
        this.paymentUrl = paymentUrl;
        this.routeId = routeId;
        this.amount = amount;
    }

    // Getters
    public String getPaymentUrl() { return paymentUrl; }
    public Long getRouteId() { return routeId; }
    public Double getAmount() { return amount; }

    // Setters
    public void setPaymentUrl(String paymentUrl) { this.paymentUrl = paymentUrl; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }
    public void setAmount(Double amount) { this.amount = amount; }

    // --- Manual Builder Implementation ---
    public static PaymentRedirectResponseBuilder builder() {
        return new PaymentRedirectResponseBuilder();
    }

    public static class PaymentRedirectResponseBuilder {
        private String paymentUrl;
        private Long routeId;
        private Double amount;

        public PaymentRedirectResponseBuilder paymentUrl(String paymentUrl) {
            this.paymentUrl = paymentUrl;
            return this;
        }

        public PaymentRedirectResponseBuilder routeId(Long routeId) {
            this.routeId = routeId;
            return this;
        }

        public PaymentRedirectResponseBuilder amount(Double amount) {
            this.amount = amount;
            return this;
        }

        public PaymentRedirectResponse build() {
            return new PaymentRedirectResponse(paymentUrl, routeId, amount);
        }
    }
}