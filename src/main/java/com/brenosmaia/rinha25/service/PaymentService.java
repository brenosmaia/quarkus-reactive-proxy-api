package com.brenosmaia.rinha25.service;

import com.brenosmaia.rinha25.dto.PaymentRequestDTO;
import com.brenosmaia.rinha25.repository.PaymentRepository;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PaymentService {

    @Inject
    private PaymentRepository paymentRepository;

    public Uni<PaymentRequestDTO> savePayment(PaymentRequestDTO payment, String paymentId) {
        return paymentRepository.save(payment, paymentId);
    }
} 