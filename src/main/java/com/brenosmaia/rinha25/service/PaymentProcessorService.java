package com.brenosmaia.rinha25.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.brenosmaia.rinha25.dto.PaymentRequestDTO;
import com.brenosmaia.rinha25.dto.PaymentsSummaryResponseDTO;
import com.brenosmaia.rinha25.dto.PaymentsSummaryResponseDTO.ProcessorStatsDTO;

@Service
public class PaymentProcessorService {

	@Value("${payment.default.url}")
	private String paymentDefaultUrl;
	@Value("${payment.fallback.url}")
	private String paymentFallbackUrl;
	
	@Value("${payments-summary.default.url}")
	private String paymentsSummaryDefaultUrl;
	@Value("${payments-summary.fallback.url}")
	private String paymentsSummaryFallbackUrl;

	RestTemplate restTemplate = new RestTemplate();
	
	public String processPayment(PaymentRequestDTO paymentRequest) {
		try {
			return callExternalPaymentService(paymentRequest, paymentDefaultUrl);
		} catch (Exception e) {
			return callExternalPaymentService(paymentRequest, paymentFallbackUrl);
		}
	}
	
	private String callExternalPaymentService(PaymentRequestDTO paymentRequest, String url) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<PaymentRequestDTO> requestEntity = new HttpEntity<>(paymentRequest, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
		
		return response.getBody();
	}
	
	public PaymentsSummaryResponseDTO getPaymentsSummary(String from, String to) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("X-Rinha-Token", "123");
		
		HttpEntity<String> requestEntity = new HttpEntity<>(headers);
		
		ProcessorStatsDTO defaultStats = getProcessorStats(paymentsSummaryDefaultUrl, requestEntity, from, to);
		ProcessorStatsDTO fallbackStats = getProcessorStats(paymentsSummaryFallbackUrl, requestEntity, from, to);
			
		return PaymentsSummaryResponseDTO.builder()
                .defaultStats(defaultStats)
                .fallbackStats(fallbackStats).build();
	}
	
	private ProcessorStatsDTO getProcessorStats(String url, HttpEntity<String> requestEntity, String from, String to) {
	    
	    ResponseEntity<ProcessorStatsDTO> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, ProcessorStatsDTO.class, from, to);
	    
	    return response.getBody();
	}
}
