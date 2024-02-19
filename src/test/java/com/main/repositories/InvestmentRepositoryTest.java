package com.main.repositories;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.main.backendtest.entities.Investment;
import com.main.backendtest.interfaces.InvestmentInterface;

@SuppressWarnings("null")
public class InvestmentRepositoryTest implements InvestmentInterface {
    protected List<Investment> investments = new ArrayList<>();

    @Override
    public Investment save(Investment investiment) {
        Investment handleInvestment = null;

        Optional<Investment> doesInvestmentExists = this.investments.stream()
                .filter(investments -> investments.getId().equals(investiment.getId())).findFirst();

        if (doesInvestmentExists.isEmpty()) {
            investiment.setId(UUID.randomUUID());

            if (investiment.getCreatedAt() == null) {
                investiment.setCreatedAt(Instant.now());
            }

            this.investments.add(investiment);

            handleInvestment = investiment;
        } else {
            int getCurrentInvestmentIdx = this.investments.indexOf(doesInvestmentExists.get());
            this.investments.set(getCurrentInvestmentIdx, investiment);

            handleInvestment = this.investments.get(getCurrentInvestmentIdx);
        }

        return handleInvestment;
    }

    @Override
    public Page<Investment> findByWalletId(UUID walletId, Pageable pageable) {
        List<Investment> userInvestments = this.investments.stream()
                .filter(investments -> investments.getWallet().getId().equals(walletId)).toList();

        int fromIndex = pageable.getPageNumber() * pageable.getPageSize();

        if (userInvestments.size() <= fromIndex) {
            return new PageImpl<Investment>(Collections.emptyList());
        }

        return new PageImpl<Investment>(userInvestments.subList(fromIndex,
                Math.min(fromIndex + pageable.getPageSize(), userInvestments.size())));
    }
}
