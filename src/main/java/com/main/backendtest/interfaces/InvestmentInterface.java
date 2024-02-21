package com.main.backendtest.interfaces;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.main.backendtest.entities.Investment;

public interface InvestmentInterface {
    Investment save(Investment investiment);

    Page<Investment> findByWalletId(UUID walletId, Pageable pageable);

    List<Investment> getAllCreatedToday();

    Optional<Investment> findById(UUID investmentId);
}
