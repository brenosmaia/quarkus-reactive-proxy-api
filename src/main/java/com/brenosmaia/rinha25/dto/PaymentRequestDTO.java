package com.brenosmaia.rinha25.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {

    @NotBlank(message = "Correlation ID is required")
    private String correlationId;
    
    @NotBlank(message = "Amount is required")
    private String amount;
}
