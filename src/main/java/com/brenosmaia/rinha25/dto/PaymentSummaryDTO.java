package com.brenosmaia.rinha25.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSummaryDTO {

    private Integer defaultTotalRequests;
    private String fallbaTotalRequests;
    private String status;
    private String createdAt;
    private String updatedAt;
}
