package com.main.backendtest.interfaces;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.main.backendtest.entities.Investment;

public interface InvestmentInterface {
    Investment save(Investment investiment);

    Page<Investment> findByWalletId(UUID walletId, Pageable pageable);
}
