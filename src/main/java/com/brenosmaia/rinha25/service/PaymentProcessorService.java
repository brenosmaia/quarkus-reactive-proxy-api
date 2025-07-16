package com.brenosmaia.rinha25.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.brenosmaia.rinha25.client.DefaultPaymentProcessorClient;
import com.brenosmaia.rinha25.client.DefaultPaymentsSummaryClient;
import com.brenosmaia.rinha25.client.FallbackPaymentProcessorClient;
import com.brenosmaia.rinha25.client.FallbackPaymentsSummaryClient;
import com.brenosmaia.rinha25.dto.PaymentRequestDTO;
import com.brenosmaia.rinha25.dto.PaymentsSummaryResponseDTO;
import com.brenosmaia.rinha25.dto.PaymentsSummaryResponseDTO.ProcessorStatsDTO;

@ApplicationScoped
public class PaymentProcessorService {

	@Inject
	@RestClient
	DefaultPaymentProcessorClient defaultPaymentsProcessor;
	
	@Inject
	@RestClient
	FallbackPaymentProcessorClient fallbackPaymentsProcessor;
	
	@Inject
	@RestClient
	DefaultPaymentsSummaryClient defaultPaymentsSummary;
	
	@Inject
	@RestClient
	FallbackPaymentsSummaryClient fallbackPaymentsSummary;
	
	public String processPayment(PaymentRequestDTO paymentRequest) {
		try {
			return defaultPaymentsProcessor.processPayment(paymentRequest);
		} catch (Exception e) {
			return fallbackPaymentsProcessor.processPayment(paymentRequest);
		}
	}
	
	public PaymentsSummaryResponseDTO getPaymentsSummary(String from, String to) {
		ProcessorStatsDTO defaultStats = defaultPaymentsSummary.getPaymentsSummary(from, to, "123");
		ProcessorStatsDTO fallbackStats = fallbackPaymentsSummary.getPaymentsSummary(from, to, "123");
			
		return new PaymentsSummaryResponseDTO(defaultStats, fallbackStats);
	}
}
