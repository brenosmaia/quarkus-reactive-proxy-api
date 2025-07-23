package com.brenosmaia.rinha25.dto;

public class HealthCheckResponseDTO {
    
    private boolean failing;
    private int minResponseTime;

    public boolean isFailing() {
        return failing;
    }

    public void setFailing(boolean failing) {
        this.failing = failing;
    }

    public int getMinResponseTime() {
        return minResponseTime;
    }

    public void setMinResponseTime(int minResponseTime) {
        this.minResponseTime = minResponseTime;
    }
}
