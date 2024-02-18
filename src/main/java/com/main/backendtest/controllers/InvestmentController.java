package com.main.backendtest.controllers;

import java.util.UUID;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.main.backendtest.dtos.request.investment.NewInvestmentDto;
import com.main.backendtest.dtos.response.NewInvestmentResponseDto;
import com.main.backendtest.entities.Investment;
import com.main.backendtest.services.InvestmentService;
import com.main.backendtest.services.JwtService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/investment")
public class InvestmentController {
    @Autowired
    private InvestmentService investmentService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/new")
    public ResponseEntity<NewInvestmentResponseDto> newInvestment(
            @RequestBody @Valid NewInvestmentDto dto,
            @RequestHeader(name = "Authorization", required = true) String authToken) {
        Investment investment = this.investmentService.create(dto, this.getParsedAuth(authToken));

        return ResponseEntity.status(201).body(this.formatNewInvestmentDto(investment));
    }

    private UUID getParsedAuth(String token) {
        String parseToken = this.jwtService.verify(token.replaceAll("Bearer ", ""));

        return UUID.fromString(parseToken);
    }

    private NewInvestmentResponseDto formatNewInvestmentDto(Investment investment) {
        return new NewInvestmentResponseDto(investment.getId(), investment.getInitialAmount(),
                investment.getCurrentProfit(), investment.getCreatedAt());
    }
}
