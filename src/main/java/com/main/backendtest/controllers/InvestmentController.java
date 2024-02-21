package com.main.backendtest.controllers;

import java.util.UUID;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import com.main.backendtest.dtos.request.investment.NewInvestmentDto;
import com.main.backendtest.dtos.response.InvestmentDto;
import com.main.backendtest.dtos.response.NewInvestmentResponseDto;
import com.main.backendtest.entities.Investment;
import com.main.backendtest.entities.Wallet;
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

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getMethodName(
            @RequestHeader(name = "Authorization", required = true) String authToken,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "perPage", required = false, defaultValue = "5") int perPage) {
        UUID parsedToken = this.getParsedAuth(authToken);

        Page<Investment> investments =
                this.investmentService.listByUser(parsedToken, page, perPage);

        Map<String, Object> responseBody = new HashMap<>();
        List<InvestmentDto> investmentsDto = this.formatInvestmentList(investments.getContent());

        responseBody.put("page", page);
        responseBody.put("perPage", perPage);
        responseBody.put("totalInvestments", investments.getTotalElements());
        responseBody.put("investments", investmentsDto);

        return ResponseEntity.ok().body(responseBody);
    }

    @PatchMapping("/withdraw/{investmentId}")
    public ResponseEntity<Wallet> withdrawInvestment(
            @PathVariable(name = "investmentId", required = true) UUID investmentId,
            @RequestHeader(name = "Authorization", required = true) String jwtToken) {
        UUID parseToken = this.getParsedAuth(jwtToken);

        Wallet performWithdraw =
                this.investmentService.withdrawnInvestment(parseToken, investmentId);

        return ResponseEntity.ok().body(performWithdraw);
    }

    private UUID getParsedAuth(String token) {
        String parseToken = this.jwtService.verify(token.replaceAll("Bearer ", ""));

        return UUID.fromString(parseToken);
    }

    private List<InvestmentDto> formatInvestmentList(List<Investment> investments) {
        return investments.stream().map(investment -> {
            return new InvestmentDto(investment.getId(), investment.getInitialAmount(),
                    investment.getCurrentProfit(), investment.getCreatedAt(),
                    investment.isAlreadyWithdrawn(), investment.getWithdrawnDate());
        }).toList();
    }

    private NewInvestmentResponseDto formatNewInvestmentDto(Investment investment) {
        return new NewInvestmentResponseDto(investment.getId(), investment.getInitialAmount(),
                investment.getCurrentProfit(), investment.getCreatedAt());
    }
}
