package com.brenosmaia.rinha25.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.brenosmaia.rinha25.client.DefaultPaymentProcessorClient;
import com.brenosmaia.rinha25.client.FallbackPaymentProcessorClient;
import com.brenosmaia.rinha25.config.RedisConfig;
import com.brenosmaia.rinha25.dto.PaymentProcessResultDTO;
import com.brenosmaia.rinha25.model.Payment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class PaymentProcessorService {
	private static final String PAYMENT_QUEUE_KEY = "paymentsQueue";

	@Inject
	@RestClient
	DefaultPaymentProcessorClient defaultPaymentsProcessor;
	
	@Inject
	@RestClient
	FallbackPaymentProcessorClient fallbackPaymentsProcessor;
	
	@Inject
    ObjectMapper objectMapper;

	@Inject
	HealthCheckService healthCheckService;

	@Inject
	RedisConfig redisConfig;

	public Uni<PaymentProcessResultDTO> processPayment(Payment payment) {
		return healthCheckService.isDefaultPaymentProcessorHealthy()
			.flatMap(isDefaultHealthy -> {
				if (isDefaultHealthy) {
					return defaultPaymentsProcessor.processPayment(payment)
						.map(unused -> new PaymentProcessResultDTO(payment.getCorrelationId(), payment.getAmount(), "default"))
						.onFailure().recoverWithUni(err ->
							tryFallbackOrQueue(payment)
						);
				} else {
					return tryFallbackOrQueue(payment);
				}
			});
	}

	public Uni<PaymentProcessResultDTO> tryFallbackOrQueue(Payment payment) {
		return healthCheckService.isFallbackPaymentProcessorHealthy()
			.flatMap(isFallbackHealthy -> {
				if (isFallbackHealthy) {
					return fallbackPaymentsProcessor.processPayment(payment)
						.map(unused -> new PaymentProcessResultDTO(payment.getCorrelationId(), payment.getAmount(), "fallback"))
						.onFailure().recoverWithUni(err -> 
							addToQueue(payment)
								.replaceWith(new PaymentProcessResultDTO(payment.getCorrelationId(), payment.getAmount(), "queued"))
						);
				} else {
					return addToQueue(payment)
						.replaceWith(new PaymentProcessResultDTO(payment.getCorrelationId(), payment.getAmount(), "queued"));
				}
			});
	}

	private Uni<Void> addToQueue(Payment payment) {
		try {
			String json = objectMapper.writeValueAsString(payment);
			return redisConfig.getReactiveRedisDataSource()
				.list(String.class, String.class)
				.lpush(PAYMENT_QUEUE_KEY, json)
				.replaceWithVoid();
		} catch (JsonProcessingException e) {
			return Uni.createFrom().failure(new RuntimeException("Failed to serialize Payment to JSON", e));
		}
	}
}
