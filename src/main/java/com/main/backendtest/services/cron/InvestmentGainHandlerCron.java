package com.main.backendtest.services.cron;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.main.backendtest.entities.Investment;
import com.main.backendtest.repositories.InvestmentRepositoryImpl;

@Component
@EnableScheduling
public class InvestmentGainHandlerCron {
    protected final InvestmentRepositoryImpl investmentRepository;

    public InvestmentGainHandlerCron(InvestmentRepositoryImpl investmentRepository) {
        this.investmentRepository = investmentRepository;
    }

    @Scheduled(cron = "@midnight", zone = "UTC")
    public void autoHandleMonthlyGains() {
        List<Investment> investmentsOfTheDay = this.investmentRepository.getAllCreatedToday();

        BigDecimal gainPercentage = new BigDecimal("0.0052"); // 0.52%

        investmentsOfTheDay = investmentsOfTheDay.stream().map(investment -> {
            if (!investment.isAlreadyWithdrawn()) {
                BigDecimal investedAmount =
                        investment.getInitialAmount().add(investment.getCurrentProfit());

                BigDecimal totalGains = investment.getCurrentProfit();

                investment.setCurrentProfit(totalGains.add(gainPercentage.multiply(investedAmount))
                        .setScale(3, RoundingMode.DOWN));
            }

            return investment;
        }).toList();

        this.investmentRepository.saveAll(investmentsOfTheDay);

        System.out.println("Daily investments done.");
    }
}
