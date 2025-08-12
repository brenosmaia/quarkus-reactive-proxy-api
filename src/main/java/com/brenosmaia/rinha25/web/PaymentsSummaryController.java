package com.brenosmaia.rinha25.web;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import com.brenosmaia.rinha25.dto.PaymentsSummaryResponseDTO;
import com.brenosmaia.rinha25.service.PaymentService;

@Path("/payments-summary")
@Produces(MediaType.APPLICATION_JSON)
public class PaymentsSummaryController {

    @Inject
    PaymentService paymentService;

    @GET
    public PaymentsSummaryResponseDTO getPaymentsSummary(@QueryParam("from") String from, @QueryParam("to") String to) {
        return paymentService.getPaymentsSummary(from, to);
    }
}