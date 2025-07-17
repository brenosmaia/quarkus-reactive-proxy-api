package com.brenosmaia.rinha25.client;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.brenosmaia.rinha25.dto.PaymentRequestDTO;
import com.brenosmaia.rinha25.dto.PaymentsSummaryResponseDTO.ProcessorStatsDTO;

@Path("/payments")
@RegisterRestClient(configKey = "DefaultPaymentProcessorClient")
public interface DefaultPaymentProcessorClient {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Uni<String> processPayment(PaymentRequestDTO paymentRequest);
} 