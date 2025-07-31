package com.brenosmaia.rinha25.service;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.brenosmaia.rinha25.client.DefaultPaymentProcessorClient;
import com.brenosmaia.rinha25.client.FallbackPaymentProcessorClient;
import com.brenosmaia.rinha25.config.RedisConfig;

import io.quarkus.scheduler.Scheduled;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HealthCheckService {

    private boolean DEFAULT_PAYMENT_PROCESSOR_HEALTHY = true;
    private boolean FALLBACK_PAYMENT_PROCESSOR_HEALTHY = true;

	@Inject
	@RestClient
	DefaultPaymentProcessorClient defaultPaymentsProcessor;
	
	@Inject
	@RestClient
	FallbackPaymentProcessorClient fallbackPaymentsProcessor;

    @Inject
    RedisConfig redisConfig;
	
    public Uni<Boolean> isDefaultPaymentProcessorHealthy() {
        return Uni.createFrom().item(DEFAULT_PAYMENT_PROCESSOR_HEALTHY);
    }

    public Uni<Boolean> isFallbackPaymentProcessorHealthy() {
        return Uni.createFrom().item(FALLBACK_PAYMENT_PROCESSOR_HEALTHY);
    }

    @Scheduled(every = "5s")
    public void refreshHealthStatus() {
        // Only execute if this is the leader instance to avoid 429 errors
        String instanceId = System.getenv("INSTANCE_ID");
        if (!"1".equals(instanceId)) {
            return;
        }

        defaultPaymentsProcessor.getHealth().subscribe().with(
            healthResponse -> {
                boolean isHealthy = healthResponse != null && !healthResponse.isFailing();
                redisConfig.getReactiveRedisDataSource()
                    .value(String.class, Boolean.class)
                    .set("DEFAULT_PAYMENT_PROCESSOR_HEALTHY", isHealthy);
            },
            failure -> {
                redisConfig.getReactiveRedisDataSource()
                    .value(String.class, Boolean.class)
                    .set("DEFAULT_PAYMENT_PROCESSOR_HEALTHY", false);
                System.err.println("Default processor health check failed: " + failure.getMessage());
            }
        );

        fallbackPaymentsProcessor.getHealth().subscribe().with(
            healthResponse -> {
                boolean isHealthy = healthResponse != null && !healthResponse.isFailing();
                redisConfig.getReactiveRedisDataSource()
                    .value(String.class, Boolean.class)
                    .set("FALLBACK_PAYMENT_PROCESSOR_HEALTHY", isHealthy);
            },
            failure -> {
                redisConfig.getReactiveRedisDataSource()
                    .value(String.class, Boolean.class)
                    .set("FALLBACK_PAYMENT_PROCESSOR_HEALTHY", false);
                System.err.println("Fallback processor health check failed: " + failure.getMessage());
            }
        );
    }
}