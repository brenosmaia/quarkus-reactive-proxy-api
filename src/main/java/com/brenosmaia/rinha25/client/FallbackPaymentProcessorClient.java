package com.brenosmaia.rinha25.client;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.brenosmaia.rinha25.dto.PaymentRequestDTO;
import com.brenosmaia.rinha25.dto.PaymentsSummaryResponseDTO.ProcessorStatsDTO;

import io.smallrye.mutiny.Uni;

@Path("/payments")
@RegisterRestClient(configKey = "FallbackPaymentProcessorClient")
public interface FallbackPaymentProcessorClient {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Uni<String> processPayment(PaymentRequestDTO paymentRequest);
} 