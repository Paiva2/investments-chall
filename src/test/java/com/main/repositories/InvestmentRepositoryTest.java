package com.main.repositories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
                investiment.setCreatedAt(this.getNowInstantInPattern());
            }

            investiment.setCurrentProfit(
                    investiment.getCurrentProfit().setScale(3, RoundingMode.DOWN));
            investiment.setInitialAmount(
                    investiment.getInitialAmount().setScale(3, RoundingMode.DOWN));

            this.investments.add(investiment);

            handleInvestment = investiment;
        } else {
            int getCurrentInvestmentIdx = this.investments.indexOf(doesInvestmentExists.get());
            this.investments.set(getCurrentInvestmentIdx, investiment);

            handleInvestment = this.investments.get(getCurrentInvestmentIdx);
        }

        return handleInvestment;
    }

    protected Instant getNowInstantInPattern() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .withZone(ZoneOffset.UTC);

        LocalDateTime ldt = LocalDateTime.parse(formatter.format(Instant.now()), formatter);

        return ldt.toInstant(ZoneId.of("Europe/London").getRules().getOffset(ldt));
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

    @Override
    public List<Investment> getAllCreatedToday() {
        throw new UnsupportedOperationException("Unimplemented method 'getAllCreatedToday'");
    }

    @Override
    public Optional<Investment> findById(UUID investmentId) {
        return this.investments.stream()
                .filter(investment -> investment.getId().equals(investmentId)).findFirst();
    }
}
