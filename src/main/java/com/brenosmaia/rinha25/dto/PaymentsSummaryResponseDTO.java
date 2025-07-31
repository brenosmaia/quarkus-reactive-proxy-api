package com.brenosmaia.rinha25.dto;

import java.math.BigDecimal;

public class PaymentsSummaryResponseDTO {

	private ProcessorStatsDTO defaultStats;
	private ProcessorStatsDTO fallbackStats;

	public PaymentsSummaryResponseDTO() {
	}

	public PaymentsSummaryResponseDTO(ProcessorStatsDTO defaultStats, ProcessorStatsDTO fallbackStats) {
		this.defaultStats = defaultStats;
		this.fallbackStats = fallbackStats;
	}

	public ProcessorStatsDTO getDefaultStats() {
		return defaultStats;
	}

	public void setDefaultStats(ProcessorStatsDTO defaultStats) {
		this.defaultStats = defaultStats;
	}

	public ProcessorStatsDTO getFallbackStats() {
		return fallbackStats;
	}

	public void setFallbackStats(ProcessorStatsDTO fallbackStats) {
		this.fallbackStats = fallbackStats;
	}

	public static class ProcessorStatsDTO {
		private int totalRequests;
		private BigDecimal totalAmount;

		public ProcessorStatsDTO() {
		}

		public ProcessorStatsDTO(int totalRequests, BigDecimal totalAmount) {
			this.totalRequests = totalRequests;
			this.totalAmount = totalAmount;
		}

		public int getTotalRequests() {
			return totalRequests;
		}

		public void setTotalRequests(int totalRequests) {
			this.totalRequests = totalRequests;
		}

		public BigDecimal getTotalAmount() {
			return totalAmount;
		}

		public void setTotalAmount(BigDecimal totalAmount) {
			this.totalAmount = totalAmount;
		}
	}
}
