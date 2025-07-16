package com.brenosmaia.rinha25.web;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.brenosmaia.rinha25.dto.PaymentRequestDTO;
import com.brenosmaia.rinha25.service.PaymentProcessorService;

@Path("/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PaymentsController {

    @Inject
    PaymentProcessorService paymentProcessorService;

    @POST
    public Response postPayment(PaymentRequestDTO paymentRequest) {
        String paymentResponse = paymentProcessorService.processPayment(paymentRequest);
        return Response.ok(paymentResponse).build();
    }
}