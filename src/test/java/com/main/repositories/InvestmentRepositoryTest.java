package com.main.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.main.backendtest.entities.Investment;
import com.main.backendtest.interfaces.InvestmentInterface;

public class InvestmentRepositoryTest implements InvestmentInterface {
    protected List<Investment> investments = new ArrayList<>();

    @Override
    public Investment save(Investment investiment) {
        Investment handleInvestment = null;

        Optional<Investment> doesInvestmentExists = this.investments.stream()
                .filter(investments -> investments.getId().equals(investiment.getId())).findFirst();

        if (doesInvestmentExists.isEmpty()) {
            investiment.setId(UUID.randomUUID());
            this.investments.add(investiment);

            handleInvestment = investiment;
        } else {
            int getCurrentInvestmentIdx = this.investments.indexOf(doesInvestmentExists.get());
            this.investments.set(getCurrentInvestmentIdx, investiment);

            handleInvestment = this.investments.get(getCurrentInvestmentIdx);
        }

        return handleInvestment;
    }
}
