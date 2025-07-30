package com.brenosmaia.rinha25.web;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.Instant;

import com.brenosmaia.rinha25.dto.PaymentRequestDTO;
import com.brenosmaia.rinha25.service.PaymentProcessorService;
import com.brenosmaia.rinha25.service.PaymentService;


@Path("/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PaymentsController {

    @Inject
    PaymentProcessorService paymentProcessorService;

    @Inject
    PaymentService paymentService;

    @POST
    public Response postPayment(PaymentRequestDTO paymentRequest) {
        paymentRequest.setRequestedAt(Instant.now());

        paymentProcessorService.processPayment(paymentRequest)
            .subscribe().with(
                result -> {
                    // Save with processor type (e.g., "default", "fallback", "queued")
                    paymentService.savePayment(paymentRequest, result.getPaymentId(), result.getProcessorType());
                },
                failure -> {
                    System.err.println("Failed to process payment: " + failure.getMessage());
                });

        return Response.ok().build();
    }
}