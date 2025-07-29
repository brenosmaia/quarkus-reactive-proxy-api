package com.brenosmaia.rinha25.worker;

import java.util.List;
import java.util.logging.Logger;

import com.brenosmaia.rinha25.config.RedisConfig;
import com.brenosmaia.rinha25.dto.PaymentRequestDTO;
import com.brenosmaia.rinha25.service.PaymentProcessorService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.scheduler.Scheduled;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PaymentsProcessorWorker {
    private static final String PAYMENTS_QUEUE = "paymentsQueue";
    
    private final Logger logger = Logger.getLogger(PaymentsProcessorWorker.class.getName());
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Inject
    private RedisConfig redisConfig;

    @Inject
    PaymentProcessorService paymentProcessorService;

    @Scheduled(every = "5s")
    public void processQueue() {
        try {
            // Recuperar uma lista de itens da fila
            Uni<List<String>> paymentDataList = redisConfig.getReactiveRedisDataSource()
                .list(String.class, String.class)
                .lrange(PAYMENTS_QUEUE, 0, -1);

            paymentDataList.subscribe().with(
                dataList -> {
                    if (dataList.isEmpty()) {
                        return;
                    }

                    dataList.forEach(data -> {
                        try {
                            PaymentRequestDTO paymentRequest = objectMapper.readValue(data, PaymentRequestDTO.class);
                            paymentProcessorService.processPayment(paymentRequest);
                        } catch (JsonProcessingException e) {
                            logger.severe("Error parsing payment data: " + e.getMessage());
                        }
                    });

                    // Remover os itens processados da fila
                    redisConfig.getReactiveRedisDataSource()
                        .list(String.class, String.class)
                        .ltrim(PAYMENTS_QUEUE, dataList.size(), -1);
                },
                failure -> {
                    logger.severe("Error recovering payments: " + failure.getMessage());
                }
            );
        } catch (Exception e) {
            logger.severe("Error processing payment queue: " + e.getMessage());
        }
    }
}
