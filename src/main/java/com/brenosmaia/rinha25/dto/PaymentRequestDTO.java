package com.brenosmaia.rinha25.dto;

import java.time.Instant;
import jakarta.validation.constraints.NotBlank;

public class PaymentRequestDTO {

    @NotBlank(message = "Correlation ID is required")
    private String correlationId;
    
    @NotBlank(message = "Amount is required")
    private String amount;

    @NotBlank(message = "RequestedAt is required")
    private Instant requestedAt;

    public PaymentRequestDTO() {
    }

    public PaymentRequestDTO(String correlationId, String amount, Instant requestedAt) {
        this.correlationId = correlationId;
        this.amount = amount;
        this.requestedAt = requestedAt;
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

    public Instant getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Instant requestedAt) {
        this.requestedAt = requestedAt;
    }
}
