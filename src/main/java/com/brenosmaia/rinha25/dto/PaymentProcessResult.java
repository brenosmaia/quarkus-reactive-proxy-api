package com.brenosmaia.rinha25.dto;

public class PaymentProcessResult {
    private String paymentId;
    private String processorType;

    public PaymentProcessResult(String paymentId, String processorType) {
        this.paymentId = paymentId;
        this.processorType = processorType;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getProcessorType() {
        return processorType;
    }

    public void setProcessorType(String processorType) {
        this.processorType = processorType;
    }
}
