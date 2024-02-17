package com.main.entities;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Investiment {
    private UUID id;

    private BigDecimal initialAmount;

    private BigDecimal currentProfit;

    private Date withdrawnDate;

    private boolean alreadyWithdrawn;

    private Date createdAt;

    // ManyToOne
    // JoinColumn wallet_id
    private Wallet wallet;
}
