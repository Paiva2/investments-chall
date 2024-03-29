package com.main.backendtest.entities;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "tb_investments")
public class Investment {
    @Id
    @GeneratedValue(generator = "uuid2")
    private UUID id;

    @Column(name = "initial_amount", nullable = false, precision = 20, scale = 3)
    private BigDecimal initialAmount;

    @Column(name = "current_profit", nullable = false, precision = 20, scale = 3)
    private BigDecimal currentProfit = new BigDecimal("0");

    @Column(name = "withdrawn_date", nullable = true)
    private Instant withdrawnDate;

    @Column(name = "already_withdrawn", nullable = false)
    private boolean alreadyWithdrawn = false;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;
}
