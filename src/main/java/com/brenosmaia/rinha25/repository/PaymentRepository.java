package com.brenosmaia.rinha25.repository;

import com.brenosmaia.rinha25.config.RedisConfig;
import com.brenosmaia.rinha25.dto.PaymentRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PaymentRepository {
    private static final String PAYMENTS_PROCESSED_KEY = "paymentsProcessed";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Inject
	RedisConfig redisConfig;

    public Uni<PaymentRequestDTO> save(PaymentRequestDTO payment, String paymentId) {
        try {
            String json = objectMapper.writeValueAsString(payment);

            redisConfig.getReactiveRedisDataSource()
				.list(String.class, String.class)
				.lpush(PAYMENTS_PROCESSED_KEY, json)
				.replaceWithVoid();

            return Uni.createFrom().item(payment);
        } catch (Exception e) {
            System.err.println("Error saving payment: " + e.getMessage());
            return Uni.createFrom().failure(e);
        }
        
    }
}