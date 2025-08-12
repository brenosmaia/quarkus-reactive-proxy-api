package com.brenosmaia.rinha25.service;

import com.brenosmaia.rinha25.dto.PaymentsSummaryResponseDTO;
import com.brenosmaia.rinha25.model.Payment;
import com.brenosmaia.rinha25.repository.PaymentRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PaymentService {

    @Inject
    private PaymentRepository paymentRepository;

    public Payment savePayment(Payment payment, String paymentId, String processorType) {
        return paymentRepository.save(payment, paymentId, processorType);
    }

    public PaymentsSummaryResponseDTO getPaymentsSummary(String from, String to) {
		PaymentsSummaryResponseDTO paymentsSummary = paymentRepository.getPaymentsSummary(from, to);
			
		return paymentsSummary;
	}
}