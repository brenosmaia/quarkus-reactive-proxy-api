package com.brenosmaia.rinha25.service;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.brenosmaia.rinha25.client.DefaultPaymentProcessorClient;
import com.brenosmaia.rinha25.client.FallbackPaymentProcessorClient;

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
	
    public Uni<Boolean> isDefaultPaymentProcessorHealthy() {
        return Uni.createFrom().item(DEFAULT_PAYMENT_PROCESSOR_HEALTHY);
    }

    public Uni<Boolean> isFallbackPaymentProcessorHealthy() {
        return Uni.createFrom().item(FALLBACK_PAYMENT_PROCESSOR_HEALTHY);
    }

    @Scheduled(every = "5s")
    public void refreshHealthStatus() {
        defaultPaymentsProcessor.getHealth().onItem().transform(healthResponse -> {
            boolean isHealthy = healthResponse != null && !healthResponse.isFailing();
            DEFAULT_PAYMENT_PROCESSOR_HEALTHY = isHealthy;
            return isHealthy;
        });

        fallbackPaymentsProcessor.getHealth().onItem().transform(healthResponse -> {
            boolean isHealthy = healthResponse != null && !healthResponse.isFailing();
            FALLBACK_PAYMENT_PROCESSOR_HEALTHY = isHealthy;
            return isHealthy;
        });
    }
}