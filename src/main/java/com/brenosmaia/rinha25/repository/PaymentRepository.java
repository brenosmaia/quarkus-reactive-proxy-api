package com.brenosmaia.rinha25.repository;

import com.brenosmaia.rinha25.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByCorrelationId(String correlationId);
    
    boolean existsByCorrelationId(String correlationId);
} 