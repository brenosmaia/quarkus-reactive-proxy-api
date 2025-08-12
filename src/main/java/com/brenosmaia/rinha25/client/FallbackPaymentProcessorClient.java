package com.brenosmaia.rinha25.client;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.brenosmaia.rinha25.dto.HealthCheckResponseDTO;
import com.brenosmaia.rinha25.model.Payment;

@Path("/payments")
@RegisterRestClient(configKey = "FallbackPaymentProcessorClient")
public interface FallbackPaymentProcessorClient {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    void processPayment(Payment paymentRequest);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/service-health")
    HealthCheckResponseDTO getHealth();
} 