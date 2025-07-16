package com.brenosmaia.rinha25.service;

import com.brenosmaia.rinha25.model.Payment;
import com.brenosmaia.rinha25.repository.PaymentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PaymentService {

    @Inject
    private PaymentRepository paymentRepository;

    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.listAll();
    }

    public Optional<Payment> getPaymentByCorrelationId(String correlationId) {
        return paymentRepository.findByCorrelationId(correlationId);
    }

    public boolean existsByCorrelationId(String correlationId) {
        return paymentRepository.existsByCorrelationId(correlationId);
    }
} 