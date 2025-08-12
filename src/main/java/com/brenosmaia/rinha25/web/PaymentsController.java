package com.brenosmaia.rinha25.web;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.Instant;

import com.brenosmaia.rinha25.dto.PaymentRequestDTO;
import com.brenosmaia.rinha25.model.Payment;
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
        Payment payment = new Payment(
            paymentRequest.getCorrelationId(),
            paymentRequest.getAmount(),
            Instant.now()
        );

        try {
            var result = paymentProcessorService.processPayment(payment);

            if (!"queued".equals(result.getProcessorType()) && result.getCorrelationId() != null) {
                paymentService.savePayment(payment, result.getCorrelationId(), result.getProcessorType());
            }

            return Response.ok().build();
        } catch (Exception e) {
            System.err.println("Failed to process payment: " + e.getMessage());
            return Response.serverError().build();
        }
    }
}