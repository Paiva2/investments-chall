package com.main.services.investment;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.test.context.ActiveProfiles;

import com.main.backendtest.dtos.request.investment.NewInvestmentDto;
import com.main.backendtest.entities.Investment;
import com.main.backendtest.entities.User;
import com.main.backendtest.entities.Wallet;
import com.main.backendtest.exceptions.BadRequestException;
import com.main.backendtest.exceptions.ForbiddenException;
import com.main.backendtest.exceptions.NotFoundException;
import com.main.backendtest.services.InvestmentService;
import com.main.repositories.InvestmentRepositoryTest;
import com.main.repositories.UserRepositoryTest;
import com.main.repositories.WalletRepositoryTest;

@ActiveProfiles("test")
public class WithdrawnInvestmentServiceTest {
    protected UserRepositoryTest userRepositoryTest;

    protected WalletRepositoryTest walletRepositoryTest;

    protected InvestmentRepositoryTest investmentRepositoryTest;

    protected InvestmentService sut;

    @BeforeEach
    public void setup() {
        this.userRepositoryTest = new UserRepositoryTest();
        this.walletRepositoryTest = new WalletRepositoryTest();
        this.investmentRepositoryTest = new InvestmentRepositoryTest();

        this.sut = new InvestmentService(this.userRepositoryTest, this.walletRepositoryTest,
                this.investmentRepositoryTest);
    }

    @Test
    @DisplayName("it should withdrawn an investment correctly - tax 22.5%")
    public void caseOne() {
        User user = this.userGenerator();

        // less than 1y
        Investment investment =
                this.investmentGenerator(user.getId(), new BigDecimal("15000.20"), 1);

        Wallet withdrawn = this.sut.withdrawnInvestment(user.getId(), investment.getId());

        Assertions.assertNotNull(withdrawn);
        Assertions.assertEquals(new BigDecimal("150.606"), withdrawn.getAmount());
    }

    @Test
    @DisplayName("it should withdrawn an investment correctly - tax 18.5%")
    public void caseTwo() {
        User user = this.userGenerator();

        // 1y - 2y
        Investment investment =
                this.investmentGenerator(user.getId(), new BigDecimal("1200.3"), 13);

        Wallet withdrawnFirst = this.sut.withdrawnInvestment(user.getId(), investment.getId());

        Assertions.assertNotNull(withdrawnFirst);
        Assertions.assertEquals(new BigDecimal("12.764"), withdrawnFirst.getAmount());
    }

    @Test
    @DisplayName("it should withdrawn an investment correctly - tax 15%")
    public void caseThree() {
        User user = this.userGenerator();

        // 2y+
        Investment investment =
                this.investmentGenerator(user.getId(), new BigDecimal("1200.3"), 38);

        Wallet withdrawnFirst = this.sut.withdrawnInvestment(user.getId(), investment.getId());

        Assertions.assertNotNull(withdrawnFirst);
        Assertions.assertEquals(new BigDecimal("17.669"), withdrawnFirst.getAmount());
    }

    @Test
    @DisplayName("it should throw an exception if investment has already been withdraw")
    public void caseFour() {
        User user = this.userGenerator();

        Investment investment = this.investmentGenerator(user.getId(), new BigDecimal("1200.3"), 1);

        this.sut.withdrawnInvestment(user.getId(), investment.getId());

        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            this.sut.withdrawnInvestment(user.getId(), investment.getId());

        });

        Assertions.assertEquals("This investment has already been withdraw.",
                exception.getMessage());
    }

    @Test
    @DisplayName("it should throw an exception if user dto id is null")
    public void caseFive() {
        User user = this.userGenerator();

        Investment investment = this.investmentGenerator(user.getId(), new BigDecimal("1200.3"), 1);

        Exception exception = Assertions.assertThrows(BadRequestException.class, () -> {
            this.sut.withdrawnInvestment(null, investment.getId());

        });

        Assertions.assertEquals("Invalid user id.", exception.getMessage());
    }

    @Test
    @DisplayName("it should throw an exception if investment dto id is null")
    public void caseSix() {
        User user = this.userGenerator();

        Exception exception = Assertions.assertThrows(BadRequestException.class, () -> {
            this.sut.withdrawnInvestment(user.getId(), null);

        });

        Assertions.assertEquals("Invalid investment id.", exception.getMessage());
    }

    @Test
    @DisplayName("it should throw an exception if user isnt found")
    public void caseSeven() {
        User user = this.userGenerator();

        Investment investment = this.investmentGenerator(user.getId(), new BigDecimal("1200.3"), 1);

        Exception exception = Assertions.assertThrows(NotFoundException.class, () -> {
            this.sut.withdrawnInvestment(UUID.randomUUID(), investment.getId());

        });

        Assertions.assertEquals("User not found.", exception.getMessage());
    }


    @Test
    @DisplayName("it should throw an exception if investment isnt found")
    public void caseEight() {
        User user = this.userGenerator();

        Exception exception = Assertions.assertThrows(NotFoundException.class, () -> {
            this.sut.withdrawnInvestment(user.getId(), UUID.randomUUID());

        });

        Assertions.assertEquals("Investment not found.", exception.getMessage());
    }

    private ZonedDateTime getNowInstantInPattern() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .withZone(ZoneOffset.UTC);

        LocalDateTime ldt = LocalDateTime.parse(formatter.format(Instant.now()), formatter);

        String instantFormatted =
                ldt.toInstant(ZoneId.of("Europe/London").getRules().getOffset(ldt)).toString();

        return Instant.parse(instantFormatted).atZone(ZoneId.of("UTC"));
    }

    public Investment investmentGenerator(UUID userId, BigDecimal value, long monthsAgo) {
        NewInvestmentDto investment = new NewInvestmentDto();

        investment.setAmount(value);

        investment.setInvestmentDate(ZonedDateTime.from(this.getNowInstantInPattern())
                .minusMonths(monthsAgo).toInstant());

        return this.sut.create(investment, userId);
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

        Wallet walletCreation = this.walletRepositoryTest.save(wallet);

        user.setWallet(walletCreation);

        return this.userRepositoryTest.save(user);
    }
}
