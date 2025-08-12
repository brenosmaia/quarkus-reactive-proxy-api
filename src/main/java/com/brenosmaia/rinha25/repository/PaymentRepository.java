package com.brenosmaia.rinha25.repository;

import java.math.BigDecimal;
import java.util.List;

import com.brenosmaia.rinha25.config.RedisConfig;
import com.brenosmaia.rinha25.dto.PaymentRequestDTO;
import com.brenosmaia.rinha25.dto.PaymentsSummaryResponseDTO;
import com.brenosmaia.rinha25.dto.PaymentsSummaryResponseDTO.ProcessorStatsDTO;
import com.brenosmaia.rinha25.model.Payment;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.mutiny.Uni;
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

    public Uni<Payment> save(Payment payment, String paymentId, String processorType) {
        try {
            String json = objectMapper.writeValueAsString(payment);
            String key = "default".equals(processorType) ? DEFAULT_PAYMENTS_PROCESSED_KEY : FALLBACK_PAYMENTS_PROCESSED_KEY;
            String countKey = key + ":count";
            String sumKey = key + ":sum";

            // Salva o pagamento na lista e incrementa os contadores
            return redisConfig.getReactiveRedisDataSource()
                .list(String.class, String.class)
                .lpush(key, json)
                .flatMap(unused ->
                    redisConfig.getReactiveRedisDataSource()
                        .value(String.class, Long.class)
                        .incr(countKey)
                )
                .flatMap(unused ->
                    redisConfig.getReactiveRedisDataSource()
                        .value(String.class, String.class)
                        .incrbyfloat(sumKey, payment.getAmount().doubleValue())
                )
                .replaceWith(payment);
        } catch (Exception e) {
            System.err.println("Error saving payment: " + e.getMessage());
            return Uni.createFrom().failure(e);
        }
    }

    public Uni<PaymentsSummaryResponseDTO> getPaymentsSummary(String from, String to) {
        String defaultCountKey = DEFAULT_PAYMENTS_PROCESSED_KEY + ":count";
        String defaultSumKey = DEFAULT_PAYMENTS_PROCESSED_KEY + ":sum";
        String fallbackCountKey = FALLBACK_PAYMENTS_PROCESSED_KEY + ":count";
        String fallbackSumKey = FALLBACK_PAYMENTS_PROCESSED_KEY + ":sum";

        Uni<Long> defaultCountUni = redisConfig.getReactiveRedisDataSource()
            .value(String.class, Long.class)
            .get(defaultCountKey)
            .map(count -> count != null ? count : 0L);

        Uni<Double> defaultSumUni = redisConfig.getReactiveRedisDataSource()
            .value(String.class, String.class)
            .get(defaultSumKey)
            .map(sum -> sum != null ? Double.parseDouble(sum) : 0.0);

        Uni<Long> fallbackCountUni = redisConfig.getReactiveRedisDataSource()
            .value(String.class, Long.class)
            .get(fallbackCountKey)
            .map(count -> count != null ? count : 0L);

        Uni<Double> fallbackSumUni = redisConfig.getReactiveRedisDataSource()
            .value(String.class, String.class)
            .get(fallbackSumKey)
            .map(sum -> sum != null ? Double.parseDouble(sum) : 0.0);

        return Uni.combine().all().unis(defaultCountUni, defaultSumUni, fallbackCountUni, fallbackSumUni).asTuple()
            .map(tuple -> {
                PaymentsSummaryResponseDTO dto = new PaymentsSummaryResponseDTO();

                PaymentsSummaryResponseDTO.ProcessorStatsDTO defaultStats = new PaymentsSummaryResponseDTO.ProcessorStatsDTO();
                defaultStats.setTotalRequests(tuple.getItem1().intValue());
                defaultStats.setTotalAmount(BigDecimal.valueOf(tuple.getItem2()));
                dto.setDefaultStats(defaultStats);

                PaymentsSummaryResponseDTO.ProcessorStatsDTO fallbackStats = new PaymentsSummaryResponseDTO.ProcessorStatsDTO();
                fallbackStats.setTotalRequests(tuple.getItem3().intValue());
                fallbackStats.setTotalAmount(BigDecimal.valueOf(tuple.getItem4()));
                dto.setFallbackStats(fallbackStats);

                return dto;
            });
    }
}