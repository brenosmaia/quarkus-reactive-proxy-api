package com.brenosmaia.rinha25.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.brenosmaia.rinha25.client.DefaultPaymentProcessorClient;
import com.brenosmaia.rinha25.client.DefaultPaymentsSummaryClient;
import com.brenosmaia.rinha25.client.FallbackPaymentProcessorClient;
import com.brenosmaia.rinha25.client.FallbackPaymentsSummaryClient;
import com.brenosmaia.rinha25.config.RedisConfig;
import com.brenosmaia.rinha25.dto.PaymentRequestDTO;
import com.brenosmaia.rinha25.dto.PaymentsSummaryResponseDTO;
import com.brenosmaia.rinha25.dto.PaymentsSummaryResponseDTO.ProcessorStatsDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class PaymentProcessorService {
	private static final String PAYMENT_QUEUE_KEY = "paymentQueue";

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
	
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Inject
	HealthCheckService healthCheckService;

	@Inject
	RedisConfig redisConfig;

	public Uni<String> processPayment(PaymentRequestDTO paymentRequest) {
		 return healthCheckService.isDefaultPaymentProcessorHealthy()
		 	.flatMap(isDefaultHealthy -> {
				if (isDefaultHealthy) {
					return defaultPaymentsProcessor.processPayment(paymentRequest);
				}

				return tryFallbackOrQueue(paymentRequest);
			});
	}

	private Uni<String> tryFallbackOrQueue(PaymentRequestDTO paymentRequest) {
		return healthCheckService.isFallbackPaymentProcessorHealthy()
			.flatMap(isFallbackHealthy -> {
				if (isFallbackHealthy) {
					return fallbackPaymentsProcessor.processPayment(paymentRequest);
				} else {
					return addToQueue(paymentRequest)
						.replaceWith("Payment queued for later processing");
				}
				
			});
	}

	private Uni<Void> addToQueue(PaymentRequestDTO paymentRequest) {
		try {
			String json = objectMapper.writeValueAsString(paymentRequest);
			return redisConfig.getReactiveRedisDataSource()
				.list(String.class, String.class)
				.lpush(PAYMENT_QUEUE_KEY, json)
				.replaceWithVoid();
		} catch (JsonProcessingException e) {
			return Uni.createFrom().failure(new RuntimeException("Failed to serialize PaymentRequestDTO to JSON", e));
		}
	}
	
	public Uni<PaymentsSummaryResponseDTO> getPaymentsSummary(String from, String to) {
		Uni<ProcessorStatsDTO> defaultStats = defaultPaymentsSummary.getPaymentsSummary(from, to, "123");
		Uni<ProcessorStatsDTO> fallbackStats = fallbackPaymentsSummary.getPaymentsSummary(from, to, "123");
			
		return Uni.combine().all().unis(defaultStats, fallbackStats)
			        .asTuple()
			        .map(tuple -> new PaymentsSummaryResponseDTO(tuple.getItem1(), tuple.getItem2()));
	}
}
