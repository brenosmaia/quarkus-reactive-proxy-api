package com.brenosmaia.rinha25.web;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.brenosmaia.rinha25.dto.PaymentsSummaryResponseDTO;
import com.brenosmaia.rinha25.service.PaymentProcessorService;

@Path("/payments-summary")
@Produces(MediaType.APPLICATION_JSON)
public class PaymentsSummaryController {

    @Inject
    PaymentProcessorService paymentProcessorService;

    @GET
    public Response getPaymentsSummary(@QueryParam("from") String from, @QueryParam("to") String to) {
        PaymentsSummaryResponseDTO response = paymentProcessorService.getPaymentsSummary(from, to);
        return Response.ok(response).build();
    }
}