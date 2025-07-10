package com.brenosmaia.rinha25.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentsSummaryResponseDTO {

	@JsonProperty("default")
	private ProcessorStatsDTO defaultStats;
	@JsonProperty("fallback")
	private ProcessorStatsDTO fallbackStats;
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class ProcessorStatsDTO {
		private int totalRequests;
		private BigDecimal totalAmount;
	}
}
