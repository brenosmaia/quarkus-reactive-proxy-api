package com.brenosmaia.rinha25.worker;

import java.util.logging.Logger;

import com.brenosmaia.rinha25.config.RedisConfig;
import com.brenosmaia.rinha25.dto.PaymentProcessResultDTO;
import com.brenosmaia.rinha25.model.Payment;
import com.brenosmaia.rinha25.service.HealthCheckService;
import com.brenosmaia.rinha25.service.PaymentProcessorService;
import com.brenosmaia.rinha25.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@Startup
public class PaymentsProcessorWorker {
    private static final String PAYMENTS_QUEUE = "paymentsQueue";

    private final Logger logger = Logger.getLogger(PaymentsProcessorWorker.class.getName());

    @Inject
    ObjectMapper objectMapper;

    @Inject
    private RedisConfig redisConfig;

    @Inject
    PaymentProcessorService paymentProcessorService;

    @Inject
    PaymentService paymentService;

    @Inject
    HealthCheckService healthCheckService;

    @PostConstruct
    void init() {
        healthCheckService.setOnProcessorHealthyCallback(this::processQueue);
    }

    public void processQueue() {
        while (true) {
            String data = redisConfig.getRedisDataSource()
                .list(String.class, String.class)
                .lindex(PAYMENTS_QUEUE, 0);

            if (data == null) {
                break;
            }

            boolean defaultHealthy = healthCheckService.isDefaultPaymentProcessorHealthy();
            boolean fallbackHealthy = healthCheckService.isFallbackPaymentProcessorHealthy();

            if (!defaultHealthy && !fallbackHealthy) {
                break;
            }

            try {
                Payment payment = objectMapper.readValue(data, Payment.class);

                PaymentProcessResultDTO result;
                if (defaultHealthy) {
                    result = paymentProcessorService.processPayment(payment);
                } else if (fallbackHealthy) {
                    result = paymentProcessorService.tryFallbackOrQueue(payment);
                } else {
                    break;
                }

                paymentService.savePayment(payment, result.getCorrelationId(), result.getProcessorType());

                redisConfig.getRedisDataSource()
                    .list(String.class, String.class)
                    .lpop(PAYMENTS_QUEUE);

            } catch (JsonProcessingException e) {
                logger.severe("Error deserializing payment: " + e.getMessage());
                redisConfig.getRedisDataSource()
                    .list(String.class, String.class)
                    .lpop(PAYMENTS_QUEUE);
            } catch (Exception e) {
                logger.severe("Error processing payment from queue: " + e.getMessage());
                break;
            }
        }
    }
}
