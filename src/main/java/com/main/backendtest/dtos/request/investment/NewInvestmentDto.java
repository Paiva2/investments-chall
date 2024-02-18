package com.main.backendtest.dtos.request.investment;

import java.time.Instant;
import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PastOrPresent;

import lombok.Data;

@Data
public class NewInvestmentDto {
    @DecimalMin("1.0")
    private BigDecimal amount;

    @PastOrPresent(message = "investmentDate can't be a future date.")
    private Instant investmentDate;
}
