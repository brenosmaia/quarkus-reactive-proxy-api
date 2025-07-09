package com.brenosmaia.rinha25.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {

    private String correlationId;
    private String amount;
    private String status;
    private String message;
    
    public PaymentResponseDTO(String correlationId, String amount) {
        this.correlationId = correlationId;
        this.amount = amount;
    }
}
