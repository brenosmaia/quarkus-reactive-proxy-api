package com.brenosmaia.rinha25.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.brenosmaia.rinha25.dto.PaymentRequestDTO;
import com.brenosmaia.rinha25.dto.PaymentsSummaryResponseDTO;
import com.brenosmaia.rinha25.service.PaymentProcessorService;

@RestController
public class PaymentsController {
	
	@Autowired
	private PaymentProcessorService paymentProcessorService;
	
	@PostMapping("/payments")
	public ResponseEntity<String> postPayment(@RequestBody PaymentRequestDTO paymentRequest) {
        
		String paymentResponse = paymentProcessorService.processPayment(paymentRequest);
		
        return ResponseEntity.ok(paymentResponse);
    }
	
	@GetMapping("/payments-summary")
	public ResponseEntity<PaymentsSummaryResponseDTO> getPaymentsSummary(@RequestParam("from") String from,
			@RequestParam("to") String to) {
        
		PaymentsSummaryResponseDTO responseEntity = paymentProcessorService.getPaymentsSummary(from, to);
		
		return ResponseEntity.ok(responseEntity);
	}
}
