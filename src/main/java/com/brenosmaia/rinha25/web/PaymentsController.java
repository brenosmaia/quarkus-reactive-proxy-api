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

        paymentProcessorService.processPayment(payment)
            .subscribe().with(
                result -> {
                    if (!"queued".equals(result.getProcessorType()) && result.getCorrelationId() != null) {
                        paymentService.savePayment(payment, result.getCorrelationId(), result.getProcessorType())
                            .subscribe().with(
                                saved -> {},
                                error -> System.err.println("Error saving payment to Redis: " + error)
                            );
                    }
                    // Se for queued, não salva como processado (já foi enfileirado em addToQueue)
                },
                failure -> {
                    System.err.println("Failed to process payment: " + failure.getMessage());
                });

        return Response.ok().build();
    }
}