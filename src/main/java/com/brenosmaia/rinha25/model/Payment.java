package com.brenosmaia.rinha25.model;

import java.time.Instant;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity(name = "payments")
public class Payment extends PanacheEntity {

    @Column(nullable = false, unique = true)
    @NotBlank
    private String correlationId;

    @Column(nullable = false)
    @NotNull
    private String amount;
    
    @Column(nullable = false)
    @NotNull
    private Instant requestedAt;

    public Payment() {
    }

    public Payment(String correlationId, String amount) {
        this.correlationId = correlationId;
        this.amount = amount;
        this.requestedAt = Instant.now();
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