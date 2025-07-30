package com.brenosmaia.rinha25.service;

import com.brenosmaia.rinha25.dto.PaymentRequestDTO;
import com.brenosmaia.rinha25.dto.PaymentsSummaryResponseDTO;
import com.brenosmaia.rinha25.dto.PaymentsSummaryResponseDTO.ProcessorStatsDTO;
import com.brenosmaia.rinha25.repository.PaymentRepository;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PaymentService {

    @Inject
    private PaymentRepository paymentRepository;

    public Uni<PaymentRequestDTO> savePayment(PaymentRequestDTO payment, String paymentId, String processorType) {
        return paymentRepository.save(payment, paymentId, processorType);
    }

    public Uni<PaymentsSummaryResponseDTO> getPaymentsSummary(String from, String to) {
		Uni<ProcessorStatsDTO> defaultStats = paymentRepository.getPaymentsSummary(from, to);
		Uni<ProcessorStatsDTO> fallbackStats = paymentRepository.getPaymentsSummary(from, to);
			
		return Uni.combine().all().unis(defaultStats, fallbackStats)
			        .asTuple()
			        .map(tuple -> new PaymentsSummaryResponseDTO(tuple.getItem1(), tuple.getItem2()));
	}
} 