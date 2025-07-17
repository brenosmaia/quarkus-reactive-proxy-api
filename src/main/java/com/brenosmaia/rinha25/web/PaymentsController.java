package com.brenosmaia.rinha25.web;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.brenosmaia.rinha25.dto.PaymentRequestDTO;
import com.brenosmaia.rinha25.service.PaymentProcessorService;

import io.smallrye.mutiny.Uni;

@Path("/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PaymentsController {

    @Inject
    PaymentProcessorService paymentProcessorService;

    @POST
    public Uni<Response> postPayment(PaymentRequestDTO paymentRequest) {
    	return paymentProcessorService.processPayment(paymentRequest)
    			.map(paymentId -> Response.ok(paymentId).build());
    }
}