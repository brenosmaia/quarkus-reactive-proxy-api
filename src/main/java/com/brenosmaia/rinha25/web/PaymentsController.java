package com.brenosmaia.rinha25.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.brenosmaia.rinha25.dto.PaymentRequestDTO;
import com.brenosmaia.rinha25.dto.PaymentResponseDTO;
import com.brenosmaia.rinha25.model.Payment;
import com.brenosmaia.rinha25.service.PaymentProcessorService;

@RestController
public class PaymentsController {
	
	@Autowired
	private PaymentProcessorService paymentProcessorService;
	
	@PostMapping("/payments")
	public ResponseEntity<PaymentResponseDTO> postPayment(@RequestBody PaymentRequestDTO paymentRequest) {
        
		PaymentResponseDTO paymentResponse = paymentProcessorService.processPayment(paymentRequest);
		
        return ResponseEntity.ok(paymentResponse);
    }
	
	@GetMapping("/payments-summary")
	public ResponseEntity<Payment> getPaymentsSummary() {
        
        // Logic for getting payment summary
        return ResponseEntity.ok(new Payment());
	}
	
}
