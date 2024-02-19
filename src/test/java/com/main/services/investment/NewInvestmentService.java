package com.main.services.investment;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.junit.jupiter.api.*;

import com.main.backendtest.entities.Investment;
import com.main.backendtest.entities.User;
import com.main.backendtest.entities.Wallet;
import com.main.backendtest.dtos.request.investment.NewInvestmentDto;
import com.main.backendtest.exceptions.BadRequestException;
import com.main.backendtest.exceptions.ForbiddenException;
import com.main.backendtest.exceptions.NotFoundException;
import com.main.backendtest.services.InvestmentService;

import com.main.repositories.InvestmentRepositoryTest;
import com.main.repositories.UserRepositoryTest;
import com.main.repositories.WalletRepositoryTest;

public class NewInvestmentService {
    private UserRepositoryTest userRepositoryTest;

    private WalletRepositoryTest walletRepositoryTest;

    private InvestmentRepositoryTest investmentRepositoryTest;

    private InvestmentService sut;

    @BeforeEach
    public void setup() throws Exception {
        this.userRepositoryTest = new UserRepositoryTest();
        this.walletRepositoryTest = new WalletRepositoryTest();
        this.investmentRepositoryTest = new InvestmentRepositoryTest();

        this.sut = new InvestmentService(this.userRepositoryTest, this.walletRepositoryTest,
                this.investmentRepositoryTest);
    }

    @Test
    @DisplayName("it should create a new investment")
    public void caseOne() {
        User user = this.userGenerator();

        NewInvestmentDto newInvestment = new NewInvestmentDto();
        newInvestment.setAmount(BigDecimal.valueOf(100.00));
        newInvestment.setInvestmentDate(Instant.parse("2024-01-01T03:25:59.887Z"));

        Investment investment = this.sut.create(newInvestment, user.getId());

        Assertions.assertNotNull(investment);
        Assertions.assertEquals(new BigDecimal("1.00"), investment.getInitialAmount()); // 100
        Assertions.assertEquals(newInvestment.getInvestmentDate(), investment.getCreatedAt());
    }

    @Test
    @DisplayName("it should create a new investment with retro gains if provided date is before today")
    public void caseTwo() {
        User user = this.userGenerator();

        NewInvestmentDto newInvestment = new NewInvestmentDto();
        newInvestment.setAmount(BigDecimal.valueOf(1000));
        // 2 months ago
        newInvestment.setInvestmentDate(ZonedDateTime.now().minusMonths(2).toInstant());

        Investment investment = this.sut.create(newInvestment, user.getId());

        Assertions.assertNotNull(investment);
        Assertions.assertEquals(new BigDecimal("10.00"), investment.getInitialAmount()); // 1000
        Assertions.assertEquals(newInvestment.getInvestmentDate(), investment.getCreatedAt());
        Assertions.assertEquals(new BigDecimal("0.10"), investment.getCurrentProfit()); // 10.42
    }

    @Test
    @DisplayName("it should throw exception if invested initial amount is less than 1")
    public void caseThree() {
        User user = this.userGenerator();

        NewInvestmentDto newInvestment = new NewInvestmentDto();
        newInvestment.setAmount(BigDecimal.valueOf(0));
        newInvestment.setInvestmentDate(Instant.parse("2024-01-01T03:25:59.887Z"));

        Exception exception = Assertions.assertThrows(BadRequestException.class, () -> {
            this.sut.create(newInvestment, user.getId());
        });

        Assertions.assertEquals("Initial investment amount can' be less than 1.",
                exception.getMessage());
    }

    @Test
    @DisplayName("it should throw exception if investment date is after today")
    public void caseFour() {
        User user = this.userGenerator();

        NewInvestmentDto newInvestment = new NewInvestmentDto();

        ZoneId sysZoneId = ZoneId.systemDefault();
        Instant nowPlusOneDay =
                Instant.now().atZone(sysZoneId).toInstant().plusSeconds(60 * 60 * 24); // 1d

        newInvestment.setAmount(BigDecimal.valueOf(100.0));
        newInvestment.setInvestmentDate(Instant.parse(nowPlusOneDay.toString()));

        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            this.sut.create(newInvestment, user.getId());
        });

        Assertions.assertEquals("Investment date can't be in the future.", exception.getMessage());
    }

    @Test
    @DisplayName("it should throw exception if investment date is after today")
    public void caseFive() {
        NewInvestmentDto newInvestment = new NewInvestmentDto();
        newInvestment.setAmount(BigDecimal.valueOf(100.0));

        Exception exception = Assertions.assertThrows(NotFoundException.class, () -> {
            this.sut.create(newInvestment, UUID.randomUUID());
        });

        Assertions.assertEquals("User not found.", exception.getMessage());
    }

    @Test
    @DisplayName("it should throw exception if for some reason user doesn't have a wallet")
    public void caseSix() {
        User user = new User();

        user.setEmail("johndoe2@test.com");
        user.setName("John Doe 2");
        user.setPasswordHash("123456");
        user.setWallet(null);

        this.userRepositoryTest.save(user);

        NewInvestmentDto newInvestment = new NewInvestmentDto();
        newInvestment.setAmount(BigDecimal.valueOf(100.0));

        Exception exception = Assertions.assertThrows(NotFoundException.class, () -> {
            this.sut.create(newInvestment, user.getId());
        });

        Assertions.assertEquals("User wallet not found.", exception.getMessage());
    }

    @Test
    @DisplayName("it should throw exception if user id DTO is null")
    public void caseSeven() {
        NewInvestmentDto newInvestment = new NewInvestmentDto();
        newInvestment.setAmount(BigDecimal.valueOf(100));

        Exception exception = Assertions.assertThrows(BadRequestException.class, () -> {
            this.sut.create(newInvestment, null);
        });

        Assertions.assertEquals("Invalid user id.", exception.getMessage());
    }

    public User userGenerator() {
        User user = new User();
        Wallet wallet = new Wallet();

        user.setEmail("johndoe@test.com");
        user.setName("John Doe");
        user.setPasswordHash("123456");
        user.setId(UUID.randomUUID());

        wallet.setAmount(BigDecimal.valueOf(0));
        wallet.setUser(user);

        user.setWallet(wallet);

        return this.userRepositoryTest.save(user);
    }
}
