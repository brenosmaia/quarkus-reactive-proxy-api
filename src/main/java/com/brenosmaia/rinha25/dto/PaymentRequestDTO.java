package com.brenosmaia.rinha25.dto;

import jakarta.validation.constraints.NotBlank;

public class PaymentRequestDTO {

    @NotBlank(message = "Correlation ID is required")
    private String correlationId;
    
    @NotBlank(message = "Amount is required")
    private String amount;

    public PaymentRequestDTO() {
    }

    public PaymentRequestDTO(String correlationId, String amount) {
        this.correlationId = correlationId;
        this.amount = amount;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
