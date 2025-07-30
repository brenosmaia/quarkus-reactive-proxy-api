package com.brenosmaia.rinha25.client;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import io.smallrye.mutiny.Uni;

import com.brenosmaia.rinha25.dto.HealthCheckResponseDTO;
import com.brenosmaia.rinha25.dto.PaymentProcessResult;
import com.brenosmaia.rinha25.dto.PaymentRequestDTO;

@Path("/payments")
@RegisterRestClient(configKey = "FallbackPaymentProcessorClient")
public interface FallbackPaymentProcessorClient {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Uni<PaymentProcessResult> processPayment(PaymentRequestDTO paymentRequest);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/service-health")
    Uni<HealthCheckResponseDTO> getHealth();
} 