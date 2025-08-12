package com.brenosmaia.rinha25.service;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.brenosmaia.rinha25.client.DefaultPaymentProcessorClient;
import com.brenosmaia.rinha25.client.FallbackPaymentProcessorClient;
import com.brenosmaia.rinha25.config.RedisConfig;

import io.quarkus.scheduler.Scheduled;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HealthCheckService {

    private boolean DEFAULT_PAYMENT_PROCESSOR_HEALTHY = true;
    private boolean FALLBACK_PAYMENT_PROCESSOR_HEALTHY = true;
    private static final String KEY_DEFAULT_PAYMENT_PROCESSOR_HEALTHY = "defaultPaymentProcessorHealthy";
    private static final String KEY_FALLBACK_PAYMENT_PROCESSOR_HEALTHY = "fallbackPaymentProcessorHealthy";

    @Inject
    @RestClient
    DefaultPaymentProcessorClient defaultPaymentsProcessor;

    @Inject
    @RestClient
    FallbackPaymentProcessorClient fallbackPaymentsProcessor;

    @Inject
    RedisConfig redisConfig;

    private Runnable onProcessorHealthyCallback;

    public void setOnProcessorHealthyCallback(Runnable callback) {
        this.onProcessorHealthyCallback = callback;
    }

    public boolean isDefaultPaymentProcessorHealthy() {
        // Se não for líder, lê do Redis
        String instanceId = System.getenv("INSTANCE_ID");
        if (!"1".equals(instanceId)) {
            Boolean healthy = redisConfig.getRedisDataSource()
                .value(String.class, Boolean.class)
                .get(KEY_DEFAULT_PAYMENT_PROCESSOR_HEALTHY);
            return healthy != null ? healthy : false;
        }
        // Se for líder, usa variável local
        return DEFAULT_PAYMENT_PROCESSOR_HEALTHY;
    }

    public boolean isFallbackPaymentProcessorHealthy() {
        String instanceId = System.getenv("INSTANCE_ID");
        if (!"1".equals(instanceId)) {
            Boolean healthy = redisConfig.getRedisDataSource()
                .value(String.class, Boolean.class)
                .get(KEY_FALLBACK_PAYMENT_PROCESSOR_HEALTHY);
            return healthy != null ? healthy : false;
        }
        return FALLBACK_PAYMENT_PROCESSOR_HEALTHY;
    }

    @Scheduled(every = "5s")
    public void refreshHealthStatus() {
        // Only execute if this is the leader instance to avoid 429 errors
        String instanceId = System.getenv("INSTANCE_ID");
        if (!"1".equals(instanceId)) {
            return;
        }

        try {
            var healthResponse = defaultPaymentsProcessor.getHealth();
            boolean previousHealthy = DEFAULT_PAYMENT_PROCESSOR_HEALTHY;
            boolean isHealthy = healthResponse != null && !healthResponse.isFailing();
            DEFAULT_PAYMENT_PROCESSOR_HEALTHY = isHealthy;

            redisConfig.getRedisDataSource()
                .value(String.class, Boolean.class)
                .set(KEY_DEFAULT_PAYMENT_PROCESSOR_HEALTHY, isHealthy);

            if (!previousHealthy && isHealthy && onProcessorHealthyCallback != null) {
                onProcessorHealthyCallback.run();
            }
        } catch (Exception e) {
            DEFAULT_PAYMENT_PROCESSOR_HEALTHY = false;
            redisConfig.getRedisDataSource()
                .value(String.class, Boolean.class)
                .set(KEY_DEFAULT_PAYMENT_PROCESSOR_HEALTHY, false);
        }

        try {
            var healthResponse = fallbackPaymentsProcessor.getHealth();
            boolean previousHealthy = FALLBACK_PAYMENT_PROCESSOR_HEALTHY;
            boolean isHealthy = healthResponse != null && !healthResponse.isFailing();
            FALLBACK_PAYMENT_PROCESSOR_HEALTHY = isHealthy;

            redisConfig.getRedisDataSource()
                .value(String.class, Boolean.class)
                .set(KEY_FALLBACK_PAYMENT_PROCESSOR_HEALTHY, isHealthy);

            if (!previousHealthy && isHealthy && onProcessorHealthyCallback != null) {
                onProcessorHealthyCallback.run();
            }
        } catch (Exception e) {
            FALLBACK_PAYMENT_PROCESSOR_HEALTHY = false;
            redisConfig.getRedisDataSource()
                .value(String.class, Boolean.class)
                .set(KEY_FALLBACK_PAYMENT_PROCESSOR_HEALTHY, false);
        }
    }
}