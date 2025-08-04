package com.brenosmaia.rinha25.model;

import java.math.BigDecimal;
import java.time.Instant;

public class Payment {

    private String correlationId;
    private BigDecimal amount;
    private Instant requestedAt;

    public Payment(String correlationId, BigDecimal amount, Instant requestedAt) {
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Instant requestedAt) {
        this.requestedAt = requestedAt;
    }
}
