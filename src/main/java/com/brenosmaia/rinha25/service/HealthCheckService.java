package com.brenosmaia.rinha25.service;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.brenosmaia.rinha25.client.DefaultPaymentProcessorClient;
import com.brenosmaia.rinha25.client.FallbackPaymentProcessorClient;
import com.brenosmaia.rinha25.config.RedisConfig;

import io.quarkus.redis.datasource.value.SetArgs;
import io.quarkus.redis.datasource.value.ValueCommands;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HealthCheckService {

    private static final String DEFAULT_PROCESSOR_HEALTH_KEY = "processorDefault:health";
    private static final String FALLBACK_PROCESSOR_HEALTH_KEY = "processorFallback:health";

	@Inject
	@RestClient
	DefaultPaymentProcessorClient defaultPaymentsProcessor;
	
	@Inject
	@RestClient
	FallbackPaymentProcessorClient fallbackPaymentsProcessor;
	
    private final RedisConfig redisConfig;

    @Inject
    public HealthCheckService(RedisConfig redisConfig) {
        this.redisConfig = redisConfig;
    }

    public Uni<Boolean> isDefaultPaymentProcessorHealthy() {
        ValueCommands<String, String> redis = redisConfig.getRedisDataSource().value(String.class);
        String cachedValue = redis.get(DEFAULT_PROCESSOR_HEALTH_KEY);

        if (cachedValue != null) {
            return Uni.createFrom().item(Boolean.parseBoolean(cachedValue));
        }

        return defaultPaymentsProcessor.getHealth().onItem().transform(healthResponse -> {
            boolean isHealthy = healthResponse != null && !healthResponse.isFailing();
            cacheHealthStatus(DEFAULT_PROCESSOR_HEALTH_KEY, isHealthy);
            return isHealthy;
        });
    }

    public Uni<Boolean> isFallbackPaymentProcessorHealthy() {
        ValueCommands<String, String> redis = redisConfig.getRedisDataSource().value(String.class);
        String cachedValue = redis.get(FALLBACK_PROCESSOR_HEALTH_KEY);

        if (cachedValue != null) {
            return Uni.createFrom().item(Boolean.parseBoolean(cachedValue));
        }

        return fallbackPaymentsProcessor.getHealth().onItem().transform(healthResponse -> {
            boolean isHealthy = healthResponse != null && !healthResponse.isFailing();
            cacheHealthStatus(FALLBACK_PROCESSOR_HEALTH_KEY, isHealthy);
            return isHealthy;
        });
    }

    private void cacheHealthStatus(String key, boolean isHealthy) {
        ValueCommands<String, String> redis = redisConfig.getRedisDataSource().value(String.class);
        SetArgs setArgs = new SetArgs().ex(5); // Expiration time set to 5 seconds
        redis.set(key, String.valueOf(isHealthy), setArgs);
    }
}