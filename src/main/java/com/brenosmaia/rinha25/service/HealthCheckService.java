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
	
    private Runnable onProcessorHealthyCallback;

    public void setOnProcessorHealthyCallback(Runnable callback) {
        this.onProcessorHealthyCallback = () -> {
            callback.run();
        };
    }

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
                boolean previousHealthy = DEFAULT_PAYMENT_PROCESSOR_HEALTHY;
                boolean isHealthy = healthResponse != null && !healthResponse.isFailing();
                DEFAULT_PAYMENT_PROCESSOR_HEALTHY = isHealthy;

                redisConfig.getReactiveRedisDataSource()
                    .value(String.class, Boolean.class)
                    .set("DEFAULT_PAYMENT_PROCESSOR_HEALTHY", isHealthy);

                if (!previousHealthy && isHealthy && onProcessorHealthyCallback != null) {
                    System.out.println("Callback de processor healthy chamado!");
                    onProcessorHealthyCallback.run();
                }
            },
            failure -> {
                DEFAULT_PAYMENT_PROCESSOR_HEALTHY = false;
                redisConfig.getReactiveRedisDataSource()
                    .value(String.class, Boolean.class)
                    .set("DEFAULT_PAYMENT_PROCESSOR_HEALTHY", false);
            }
        );

        fallbackPaymentsProcessor.getHealth().subscribe().with(
            healthResponse -> {
                boolean previousHealthy = FALLBACK_PAYMENT_PROCESSOR_HEALTHY;
                boolean isHealthy = healthResponse != null && !healthResponse.isFailing();
                FALLBACK_PAYMENT_PROCESSOR_HEALTHY = isHealthy;

                redisConfig.getReactiveRedisDataSource()
                    .value(String.class, Boolean.class)
                    .set("FALLBACK_PAYMENT_PROCESSOR_HEALTHY", isHealthy);

                if (!previousHealthy && isHealthy && onProcessorHealthyCallback != null) {
                    onProcessorHealthyCallback.run();
                }
            },
            failure -> {
                FALLBACK_PAYMENT_PROCESSOR_HEALTHY = false;
                redisConfig.getReactiveRedisDataSource()
                    .value(String.class, Boolean.class)
                    .set("FALLBACK_PAYMENT_PROCESSOR_HEALTHY", false);
            }
        );
    }
}