package com.main.backendtest.services;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.Optional;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import org.springframework.stereotype.Service;

import com.main.backendtest.entities.Investment;
import com.main.backendtest.entities.User;
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
    public UserInterface userRepository;

    public WalletInterface walletRepository;

    public InvestmentInterface investmentRepository;

    public InvestmentService(UserInterface userRepository, WalletInterface walletRepository,
            InvestmentInterface investmentRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.investmentRepository = investmentRepository;
    }

    // TODO: FIX RETRO INVESTMENT GAINS
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

        if (dto.getInvestmentDate() != null) {
            if (dto.getInvestmentDate().isAfter(Instant.now())) {
                throw new ForbiddenException("Investment date can't be in the future.");
            }

            try {
                newInvestment.setCreatedAt(Instant.parse(dto.getInvestmentDate().toString()));
            } catch (DateTimeParseException exception) {
                System.err.println(exception);
                throw new BadRequestException("Invalid date.");
            }
        }

        newInvestment.setWallet(doesUserExists.get().getWallet());
        newInvestment.setInitialAmount(dto.getAmount());

        return this.investmentRepository.save(newInvestment);
    }
}
