package com.brenosmaia.rinha25.repository;

import com.brenosmaia.rinha25.model.Payment;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class PaymentRepository implements PanacheRepository<Payment> {

    public Optional<Payment> findByCorrelationId(String correlationId) {
        return find("correlationId", correlationId).firstResultOptional();
    }
    
    public boolean existsByCorrelationId(String correlationId) {
        return count("correlationId", correlationId) > 0;
    }
    
    public Payment save(Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException("Payment cannot be null");
        }
        persist(payment);
        return payment;
    }
}