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
import io.smallrye.mutiny.Uni;
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
        processNextPayment();
    }

    private void processNextPayment() {
        redisConfig.getReactiveRedisDataSource()
            .list(String.class, String.class)
            .lindex(PAYMENTS_QUEUE, 0)
            .subscribe().with(
                data -> {
                    if (data == null) {
                        return; // Fila vazia
                    }

                    Uni.combine().all().unis(
                        healthCheckService.isDefaultPaymentProcessorHealthy(),
                        healthCheckService.isFallbackPaymentProcessorHealthy()
                    ).asTuple().subscribe().with(
                        healthTuple -> {
                            boolean defaultHealthy = healthTuple.getItem1();
                            boolean fallbackHealthy = healthTuple.getItem2();

                            try {
                                Payment payment = objectMapper.readValue(data, Payment.class);

                                Uni<PaymentProcessResultDTO> processUni;
                                if (defaultHealthy) {
                                    processUni = paymentProcessorService.processPayment(payment);
                                } else if (fallbackHealthy) {
                                    processUni = paymentProcessorService.tryFallbackOrQueue(payment);
                                } else {
                                    return;
                                }

                                processUni.subscribe().with(
                                    result -> {
                                        paymentService.savePayment(payment, result.getCorrelationId(), result.getProcessorType())
                                            .subscribe().with(
                                                saved -> {
                                                    redisConfig.getReactiveRedisDataSource()
                                                        .list(String.class, String.class)
                                                        .lpop(PAYMENTS_QUEUE)
                                                        .subscribe().with(
                                                            poppedValue -> {
                                                                processNextPayment();
                                                            },
                                                            error -> logger.severe("Error removing item from queue: " + error)
                                                        );
                                                },
                                                error -> logger.severe("Error saving to Redis: " + error)
                                            );
                                    },
                                    failure -> {
                                        logger.severe("Error processing payment from queue: " + failure);
                                        // Não remove da fila em caso de falha
                                        // processNextPayment() não é chamado, então a fila fica parada
                                    }
                                );
                            } catch (JsonProcessingException e) {
                                logger.severe("Error deserializing payment: " + e.getMessage());
                                // Remove invalid item from queue
                                redisConfig.getReactiveRedisDataSource()
                                    .list(String.class, String.class)
                                    .lpop(PAYMENTS_QUEUE)
                                    .subscribe().with(
                                        ignored -> processNextPayment(),
                                        error -> logger.severe("Error removing invalid item from queue: " + error)
                                    );
                            }
                        },
                        failure -> logger.severe("Error checking health of processors: " + failure)
                    );
                },
                failure -> logger.severe("Error retrieving payment from queue: " + failure)
            );
    }
}
