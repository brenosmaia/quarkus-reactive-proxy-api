package com.brenosmaia.rinha25.dto;

public class PaymentSummaryDTO {

    private Integer defaultTotalRequests;
    private String fallbaTotalRequests;
    private String status;
    private String createdAt;
    private String updatedAt;

    public PaymentSummaryDTO() {
    }

    public PaymentSummaryDTO(Integer defaultTotalRequests, String fallbaTotalRequests, String status, String createdAt, String updatedAt) {
        this.defaultTotalRequests = defaultTotalRequests;
        this.fallbaTotalRequests = fallbaTotalRequests;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getDefaultTotalRequests() {
        return defaultTotalRequests;
    }

    public void setDefaultTotalRequests(Integer defaultTotalRequests) {
        this.defaultTotalRequests = defaultTotalRequests;
    }

    public String getFallbaTotalRequests() {
        return fallbaTotalRequests;
    }

    public void setFallbaTotalRequests(String fallbaTotalRequests) {
        this.fallbaTotalRequests = fallbaTotalRequests;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
