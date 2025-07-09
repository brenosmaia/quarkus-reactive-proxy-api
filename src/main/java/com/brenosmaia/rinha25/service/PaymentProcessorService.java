package com.brenosmaia.rinha25.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.brenosmaia.rinha25.dto.PaymentRequestDTO;
import com.brenosmaia.rinha25.dto.PaymentResponseDTO;

@Service
public class PaymentProcessorService {

	public PaymentResponseDTO processPayment(PaymentRequestDTO paymentRequest) {
		String urlDefault = "http://localhost:8001/payments";
		String urlFallback = "http://localhost:8002/payments";
		
		try {
			return callExternalPaymentService(paymentRequest, urlDefault);
		} catch (Exception e) {
			return callExternalPaymentService(paymentRequest, urlFallback);
		}
	}
	
	private PaymentResponseDTO callExternalPaymentService(PaymentRequestDTO paymentRequest, String url) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<PaymentRequestDTO> requestEntity = new HttpEntity<>(paymentRequest, headers);
		ResponseEntity<PaymentResponseDTO> response = restTemplate.postForEntity(url, requestEntity, PaymentResponseDTO.class);
		
		return response.getBody();
	}
}
