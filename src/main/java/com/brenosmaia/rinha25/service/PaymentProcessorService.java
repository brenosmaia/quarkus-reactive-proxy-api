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

    public PaymentProcessResultDTO processPayment(Payment payment) {
        boolean isDefaultHealthy = healthCheckService.isDefaultPaymentProcessorHealthy();
        if (isDefaultHealthy) {
            try {
                defaultPaymentsProcessor.processPayment(payment);
				PaymentProcessResultDTO result = new PaymentProcessResultDTO(payment.getCorrelationId(), payment.getAmount(), "default");
                return result;
            } catch (Exception err) {
                 return tryFallbackOrQueue(payment);
            }
        }

		return null;
    }

    public PaymentProcessResultDTO tryFallbackOrQueue(Payment payment) {
        boolean isFallbackHealthy = healthCheckService.isFallbackPaymentProcessorHealthy();
        if (isFallbackHealthy) {
            try {
                fallbackPaymentsProcessor.processPayment(payment);
				PaymentProcessResultDTO result = new PaymentProcessResultDTO(payment.getCorrelationId(), payment.getAmount(), "fallback");
                return result;
            } catch (Exception err) {
                addToQueue(payment);
                return new PaymentProcessResultDTO(payment.getCorrelationId(), payment.getAmount(), "queued");
            }
        } else {
            addToQueue(payment);
            return new PaymentProcessResultDTO(payment.getCorrelationId(), payment.getAmount(), "queued");
        }
    }

    private void addToQueue(Payment payment) {
        try {
            String json = objectMapper.writeValueAsString(payment);
            redisConfig.getRedisDataSource()
                .list(String.class, String.class)
                .lpush(PAYMENT_QUEUE_KEY, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize Payment to JSON", e);
        }
    }
}
