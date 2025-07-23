package com.brenosmaia.rinha25.web;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import com.brenosmaia.rinha25.dto.PaymentsSummaryResponseDTO;
import com.brenosmaia.rinha25.service.PaymentProcessorService;

import io.smallrye.mutiny.Uni;

@Path("/payments-summary")
@Produces(MediaType.APPLICATION_JSON)
public class PaymentsSummaryController {

    @Inject
    PaymentProcessorService paymentProcessorService;

    @GET
    public Uni<PaymentsSummaryResponseDTO> getPaymentsSummary(@QueryParam("from") String from, @QueryParam("to") String to) {
        return paymentProcessorService.getPaymentsSummary(from, to);
    }
}