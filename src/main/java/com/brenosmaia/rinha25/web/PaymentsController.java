package com.brenosmaia.rinha25.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brenosmaia.rinha25.model.Payment;

@RestController
public class PaymentsController {
	
	@PostMapping("/payments")
	public ResponseEntity<Payment> postPayment() {
        
		// Logic for posting a payment
        return ResponseEntity.ok(new Payment());
    }
	
	@GetMapping("/payments-summary")
	public ResponseEntity<Payment> getPaymentsSummary() {
        
        // Logic for getting payment summary
        return ResponseEntity.ok(new Payment());
	}
	
}
