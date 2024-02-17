package com.main.entities;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {
    private UUID id;

    private Date createdAt;

    private Date updatedAt;

    private BigDecimal amount;

    // OneToOne
    // JoinColumn user_id
    private User user;

    // OneToMany
    private List<Investiment> investiments;
}
