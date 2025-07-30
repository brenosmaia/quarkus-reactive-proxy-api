package com.brenosmaia.rinha25.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.brenosmaia.rinha25.client.DefaultPaymentProcessorClient;
import com.brenosmaia.rinha25.client.FallbackPaymentProcessorClient;
import com.brenosmaia.rinha25.config.RedisConfig;
import com.brenosmaia.rinha25.dto.PaymentProcessResult;
import com.brenosmaia.rinha25.dto.PaymentRequestDTO;
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
	
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Inject
	HealthCheckService healthCheckService;

	@Inject
	RedisConfig redisConfig;

	public Uni<PaymentProcessResult> processPayment(PaymentRequestDTO paymentRequest) {
		return healthCheckService.isDefaultPaymentProcessorHealthy()
			.flatMap(isDefaultHealthy -> {
				if (isDefaultHealthy) {
					return defaultPaymentsProcessor.processPayment(paymentRequest)
						.onFailure().recoverWithUni(err -> 
							addToQueue(paymentRequest)
								.replaceWith(new PaymentProcessResult(null, "queued"))
						)
						.map(result -> {
							// If successful, mark as default
							result.setProcessorType("default");
							return result;
						});
				}
				return tryFallbackOrQueue(paymentRequest);
			});
	}

	private Uni<PaymentProcessResult> tryFallbackOrQueue(PaymentRequestDTO paymentRequest) {
		return healthCheckService.isFallbackPaymentProcessorHealthy()
			.flatMap(isFallbackHealthy -> {
				if (isFallbackHealthy) {
					return fallbackPaymentsProcessor.processPayment(paymentRequest)
						.onFailure().recoverWithUni(err -> 
							addToQueue(paymentRequest)
								.replaceWith(new PaymentProcessResult(null, "queued"))
						)
						.map(result -> {
							// If successful, mark as fallback
							result.setProcessorType("fallback");
							return result;
						});
				} else {
					return addToQueue(paymentRequest)
						.replaceWith(new PaymentProcessResult(null, "queued"));
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
}
