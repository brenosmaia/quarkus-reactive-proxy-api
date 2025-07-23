package com.brenosmaia.rinha25.client;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.brenosmaia.rinha25.dto.PaymentsSummaryResponseDTO.ProcessorStatsDTO;

import io.smallrye.mutiny.Uni;

@Path("/admin/payments-summary")
@RegisterRestClient(configKey = "DefaultPaymentsSummaryClient")
public interface DefaultPaymentsSummaryClient {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    Uni<ProcessorStatsDTO> getPaymentsSummary(@QueryParam("from") String from, @QueryParam("to") String to, @HeaderParam("X-Rinha-Token") String token);
} 