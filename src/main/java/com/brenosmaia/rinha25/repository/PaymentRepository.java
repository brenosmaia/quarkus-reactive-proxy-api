package com.brenosmaia.rinha25.repository;

import java.math.BigDecimal;
import java.util.List;

import com.brenosmaia.rinha25.config.RedisConfig;
import com.brenosmaia.rinha25.dto.PaymentRequestDTO;
import com.brenosmaia.rinha25.dto.PaymentsSummaryResponseDTO;
import com.brenosmaia.rinha25.dto.PaymentsSummaryResponseDTO.ProcessorStatsDTO;
import com.brenosmaia.rinha25.model.Payment;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PaymentRepository {
    private static final String DEFAULT_PAYMENTS_PROCESSED_KEY = "defaultPaymentsProcessed";
    private static final String FALLBACK_PAYMENTS_PROCESSED_KEY = "fallbackPaymentsProcessed";

    @Inject
    ObjectMapper objectMapper;

    @Inject
    RedisConfig redisConfig;

    public Payment save(Payment payment, String paymentId, String processorType) {
        try {
            String json = objectMapper.writeValueAsString(payment);
            String key = "default".equals(processorType) ? DEFAULT_PAYMENTS_PROCESSED_KEY : FALLBACK_PAYMENTS_PROCESSED_KEY;
            redisConfig.getRedisDataSource().list(String.class, String.class).lpush(key, json);
            return payment;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public PaymentsSummaryResponseDTO getPaymentsSummary(String from, String to) {
        try {
            List<String> defaultPayments = redisConfig.getRedisDataSource().list(String.class, String.class)
                .lrange(DEFAULT_PAYMENTS_PROCESSED_KEY, 0, -1);
            List<String> fallbackPayments = redisConfig.getRedisDataSource().list(String.class, String.class)
                .lrange(FALLBACK_PAYMENTS_PROCESSED_KEY, 0, -1);

            PaymentsSummaryResponseDTO processorStats = new PaymentsSummaryResponseDTO();

            ProcessorStatsDTO defaultStats = new ProcessorStatsDTO();
            defaultStats.setTotalRequests(defaultPayments.size());
            BigDecimal defaultTotalAmount = defaultPayments.stream()
                .map(payment -> {
                    try {
                        BigDecimal amount = objectMapper.readValue(payment, PaymentRequestDTO.class).getAmount();
                        return amount != null ? amount : BigDecimal.ZERO;
                    } catch (Exception e) {
                        System.err.println("Error parsing payment [" + payment + "]: " + e.getMessage());
                        return BigDecimal.ZERO;
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            defaultStats.setTotalAmount(defaultTotalAmount);
            processorStats.setDefaultStats(defaultStats);

            ProcessorStatsDTO fallbackStats = new ProcessorStatsDTO();
            fallbackStats.setTotalRequests(fallbackPayments.size());
            BigDecimal fallbackTotalAmount = fallbackPayments.stream()
                .map(payment -> {
                    try {
                        BigDecimal amount = objectMapper.readValue(payment, PaymentRequestDTO.class).getAmount();
                        return amount != null ? amount : BigDecimal.ZERO;
                    } catch (Exception e) {
                        System.err.println("Error parsing payment [" + payment + "]: " + e.getMessage());
                        return BigDecimal.ZERO;
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            fallbackStats.setTotalAmount(fallbackTotalAmount);
            processorStats.setFallbackStats(fallbackStats);

            return processorStats;
        } catch (Exception e) {
            System.err.println("Error getting payments summary: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}