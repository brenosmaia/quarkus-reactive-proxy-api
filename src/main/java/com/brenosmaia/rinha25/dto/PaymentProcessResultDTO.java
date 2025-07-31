package com.brenosmaia.rinha25.dto;

import java.math.BigDecimal;

public class PaymentProcessResultDTO {
    private String correlationId;
    private BigDecimal amount;
    private String processorType;

    public PaymentProcessResultDTO(String correlationId, BigDecimal amount, String processorType) {
        this.correlationId = correlationId;
        this.amount = amount;
        this.processorType = processorType;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getProcessorType() {
        return processorType;
    }

    public void setProcessorType(String processorType) {
        this.processorType = processorType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
