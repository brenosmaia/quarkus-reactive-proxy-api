package com.brenosmaia.rinha25.client;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.brenosmaia.rinha25.dto.HealthCheckResponseDTO;
import com.brenosmaia.rinha25.dto.PaymentProcessResultDTO;
import com.brenosmaia.rinha25.model.Payment;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/payments")
@RegisterRestClient(configKey = "DefaultPaymentProcessorClient")
public interface DefaultPaymentProcessorClient {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Uni<PaymentProcessResultDTO> processPayment(Payment paymentRequest);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/service-health")
    Uni<HealthCheckResponseDTO> getHealth();
} 