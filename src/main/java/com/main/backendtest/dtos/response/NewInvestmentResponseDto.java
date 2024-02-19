package com.main.backendtest.dtos.response;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

import lombok.Data;

@Data
public class NewInvestmentResponseDto {
    private UUID id;

    private BigDecimal initialAmount;

    private BigDecimal currentProfit;

    private BigDecimal total;

    private Instant createdAt;

    public NewInvestmentResponseDto(UUID id, BigDecimal initialAmount, BigDecimal currentProfit,
            Instant createdAt) {
        this.id = id;
        this.initialAmount = this.multiplyForView(initialAmount);
        this.currentProfit = this.multiplyForView(currentProfit);
        this.total = this.multiplyForView(initialAmount.add(currentProfit));
        this.createdAt = createdAt;
    };

    private BigDecimal multiplyForView(BigDecimal value) {
        return value.multiply(new BigDecimal("100")).setScale(2, RoundingMode.DOWN);
    }
}
