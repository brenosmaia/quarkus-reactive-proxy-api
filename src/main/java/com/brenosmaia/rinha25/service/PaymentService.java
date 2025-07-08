package com.brenosmaia.rinha25.service;

import com.brenosmaia.rinha25.model.Payment;
import com.brenosmaia.rinha25.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Optional<Payment> getPaymentByCorrelationId(String correlationId) {
        return paymentRepository.findByCorrelationId(correlationId);
    }

    public boolean existsByCorrelationId(String correlationId) {
        return paymentRepository.existsByCorrelationId(correlationId);
    }
} 