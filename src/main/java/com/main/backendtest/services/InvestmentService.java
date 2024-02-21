package com.main.backendtest.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import java.util.Optional;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.ZoneOffset;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.main.backendtest.entities.Investment;
import com.main.backendtest.entities.User;
import com.main.backendtest.entities.Wallet;
import com.main.backendtest.dtos.request.investment.NewInvestmentDto;
import com.main.backendtest.exceptions.BadRequestException;
import com.main.backendtest.exceptions.ForbiddenException;
import com.main.backendtest.exceptions.NotFoundException;
import com.main.backendtest.interfaces.InvestmentInterface;
import com.main.backendtest.interfaces.UserInterface;
import com.main.backendtest.interfaces.WalletInterface;

import jakarta.transaction.Transactional;

@Service
public class InvestmentService {
    protected UserInterface userRepository;

    protected WalletInterface walletRepository;

    protected InvestmentInterface investmentRepository;

    protected DateTimeFormatter dateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public InvestmentService(UserInterface userRepository, WalletInterface walletRepository,
            InvestmentInterface investmentRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.investmentRepository = investmentRepository;
    }

    @Transactional
    public Investment create(NewInvestmentDto dto, UUID userId) {
        if (userId == null) {
            throw new BadRequestException("Invalid user id.");
        }

        if (dto.getAmount().compareTo(BigDecimal.valueOf(1)) < 0) {
            throw new BadRequestException("Initial investment amount can' be less than 1.");
        }

        Optional<User> doesUserExists = this.userRepository.findById(userId);

        if (doesUserExists.isEmpty()) {
            throw new NotFoundException("User not found.");
        }

        if (doesUserExists.get().getWallet() == null) {
            throw new NotFoundException("User wallet not found.");
        }

        Investment newInvestment = new Investment();

        boolean doesInvestmentHasPreviousDate = dto.getInvestmentDate() != null;

        if (doesInvestmentHasPreviousDate) {
            if (dto.getInvestmentDate().isAfter(Instant.now())) {
                throw new ForbiddenException("Investment date can't be in the future.");
            }

            try {
                BigDecimal initAmount = dto.getAmount().divide(new BigDecimal("100"));
                long hasRetroGains = this.handleRetroativeMonths(dto.getInvestmentDate());

                if (hasRetroGains > 0) {
                    BigDecimal gainPercentage = new BigDecimal("0.0052");
                    BigDecimal totalGains = new BigDecimal("0");

                    Number[] months = new Number[Integer.valueOf(Long.toString(hasRetroGains))];

                    for (int i = 0; i <= months.length - 1; i++) {
                        totalGains = totalGains.add(gainPercentage.multiply(initAmount));
                        initAmount = initAmount.add(totalGains);
                    }

                    newInvestment.setCurrentProfit(totalGains.setScale(3, RoundingMode.DOWN));
                }
            } catch (DateTimeParseException exception) {
                System.err.println(exception);
                throw new BadRequestException("Invalid date.");
            }
        }


        newInvestment.setCreatedAt(doesInvestmentHasPreviousDate ? dto.getInvestmentDate()
                : this.getNowInstantInPattern());


        newInvestment.setWallet(doesUserExists.get().getWallet());

        newInvestment.setInitialAmount(
                dto.getAmount().divide(new BigDecimal("100")).setScale(3, RoundingMode.DOWN));

        return this.investmentRepository.save(newInvestment);
    }


    public Page<Investment> listByUser(UUID userId, int page, int pageSize) {
        if (userId == null) {
            throw new BadRequestException("Invalid user id.");
        }

        if (page < 1) {
            page = 1;
        }

        if (pageSize < 5) {
            pageSize = 5;
        }

        Optional<User> doesInvestorExists = this.userRepository.findById(userId);

        if (doesInvestorExists.isEmpty()) {
            throw new NotFoundException("User not found.");
        }

        Pageable pageable = PageRequest.of((page - 1), pageSize);

        return this.investmentRepository
                .findByWalletId(doesInvestorExists.get().getWallet().getId(), pageable);
    }

    @Transactional
    public Wallet withdrawnInvestment(UUID userId, UUID investmentId) {
        if (userId == null) {
            throw new BadRequestException("Invalid user id.");
        }

        if (investmentId == null) {
            throw new BadRequestException("Invalid investment id.");
        }

        Optional<User> doesUserExists = this.userRepository.findById(userId);

        if (doesUserExists.isEmpty()) {
            throw new NotFoundException("User not found.");
        }

        Optional<Investment> doesInvestmentExists =
                this.investmentRepository.findById(investmentId);

        if (doesInvestmentExists.isEmpty()) {
            throw new NotFoundException("Investment not found.");
        }

        Wallet wallet = doesUserExists.get().getWallet();

        Investment investment = doesInvestmentExists.get();

        if (investment.getWithdrawnDate() != null && investment.isAlreadyWithdrawn()) {
            throw new ForbiddenException("This investment has already been withdraw.");
        }

        long getInvestmentAge = this.handleInvestmentAge(investment.getCreatedAt().toString());

        BigDecimal taxPercentage = this.handleInvestmentAgePercentage(getInvestmentAge);

        BigDecimal investmentTotal =
                investment.getInitialAmount().add(investment.getCurrentProfit());

        BigDecimal taxOverProfits = taxPercentage.multiply(investment.getCurrentProfit());

        BigDecimal investmentTotalTaxed = investmentTotal.subtract(taxOverProfits);

        wallet.setAmount(
                wallet.getAmount().add(investmentTotalTaxed).setScale(3, RoundingMode.DOWN));

        investment.setAlreadyWithdrawn(true);
        investment.setWithdrawnDate(Instant.now());

        this.investmentRepository.save(investment);

        return this.walletRepository.save(wallet);
    }


    private Instant getNowInstantInPattern() {
        DateTimeFormatter formatter = this.dateTimeFormatter.withZone(ZoneOffset.UTC);

        LocalDateTime ldt = LocalDateTime.parse(formatter.format(Instant.now()), formatter);

        return ldt.toInstant(ZoneId.of("Europe/London").getRules().getOffset(ldt));
    }

    private long handleRetroativeMonths(Instant investmentDate) {
        ZoneId zoneId = ZoneId.systemDefault();

        Instant providedDate = Instant.parse(investmentDate.toString());

        LocalDateTime providedDateToCompare =
                LocalDateTime.ofInstant(Instant.parse(providedDate.toString()), zoneId);

        return ChronoUnit.MONTHS.between(providedDateToCompare, LocalDateTime.now());
    }

    private BigDecimal handleInvestmentAgePercentage(long investmentAge) {
        BigDecimal taxPercentage = null;

        if (investmentAge < 1) {
            taxPercentage = new BigDecimal("0.225");
        } else if (investmentAge >= 1 && investmentAge <= 2) {
            taxPercentage = new BigDecimal("0.185");
        } else {
            taxPercentage = new BigDecimal("0.15");
        }

        return taxPercentage;
    }

    private long handleInvestmentAge(String date) {
        String investmentDate = date.replaceFirst(" ", "T").replace(" +0000", "Z");

        DateTimeFormatter formatter = this.dateTimeFormatter;

        LocalDateTime parsedInvestmentDate = LocalDateTime.parse(investmentDate, formatter);

        return ChronoUnit.YEARS.between(parsedInvestmentDate, LocalDateTime.now());
    }
}
