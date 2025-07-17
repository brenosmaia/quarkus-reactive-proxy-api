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

import io.smallrye.mutiny.Uni;

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
	
	public Uni<String> processPayment(PaymentRequestDTO paymentRequest) {
		 return defaultPaymentsProcessor.processPayment(paymentRequest)
			        .onFailure()
			        .recoverWithUni(throwable -> fallbackPaymentsProcessor.processPayment(paymentRequest));
	}
	
	public Uni<PaymentsSummaryResponseDTO> getPaymentsSummary(String from, String to) {
		Uni<ProcessorStatsDTO> defaultStats = defaultPaymentsSummary.getPaymentsSummary(from, to, "123");
		Uni<ProcessorStatsDTO> fallbackStats = fallbackPaymentsSummary.getPaymentsSummary(from, to, "123");
			
		return Uni.combine().all().unis(defaultStats, fallbackStats)
			        .asTuple()
			        .map(tuple -> new PaymentsSummaryResponseDTO(tuple.getItem1(), tuple.getItem2()));
	}
}
