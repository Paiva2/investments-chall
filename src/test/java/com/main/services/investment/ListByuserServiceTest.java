package com.main.services.investment;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;

import com.main.backendtest.dtos.request.investment.NewInvestmentDto;
import com.main.backendtest.entities.Investment;
import com.main.backendtest.entities.User;
import com.main.backendtest.entities.Wallet;
import com.main.backendtest.exceptions.BadRequestException;
import com.main.backendtest.exceptions.NotFoundException;
import com.main.backendtest.services.InvestmentService;
import com.main.repositories.InvestmentRepositoryTest;
import com.main.repositories.UserRepositoryTest;
import com.main.repositories.WalletRepositoryTest;

@ActiveProfiles("test")
public class ListByuserServiceTest {
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
        @DisplayName("it should list all user investments")
        public void caseOne() {
                User user = this.userGenerator();

                this.investmentGenerator(user.getId(), new BigDecimal("100.30"));
                this.investmentGenerator(user.getId(), new BigDecimal("130.20"));
                this.investmentGenerator(user.getId(), new BigDecimal("2000000.50"));

                int page = 1;
                int perPage = 5;

                Page<Investment> investments = this.sut.listByUser(user.getId(), page, perPage);

                Investment firstInvestment = investments.getContent().get(0);
                Investment secondInvestment = investments.getContent().get(1);
                Investment thirdInvestment = investments.getContent().get(2);

                ZonedDateTime firstInvestmentDate = ZonedDateTime
                                .ofInstant(firstInvestment.getCreatedAt(), ZoneId.systemDefault());

                ZonedDateTime secondInvestmentDate = ZonedDateTime
                                .ofInstant(secondInvestment.getCreatedAt(), ZoneId.systemDefault());

                ZonedDateTime thirdInvestmentDate = ZonedDateTime
                                .ofInstant(thirdInvestment.getCreatedAt(), ZoneId.systemDefault());

                Assertions.assertAll("investment assertions",
                                // First Investment
                                () -> assertEquals(
                                                new BigDecimal("100.30")
                                                                .divide(new BigDecimal("100"))
                                                                .setScale(3, RoundingMode.DOWN),
                                                firstInvestment.getInitialAmount()),

                                () -> assertEquals(Calendar.MONTH,
                                                firstInvestmentDate.getMonthValue()),

                                () -> assertEquals(
                                                Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
                                                firstInvestmentDate.getDayOfMonth()),

                                () -> assertEquals(Calendar.getInstance().get(Calendar.YEAR),
                                                firstInvestmentDate.getYear()),

                                // Second investment
                                () -> assertEquals(
                                                new BigDecimal("130.20")
                                                                .divide(new BigDecimal("100"))
                                                                .setScale(3, RoundingMode.DOWN),
                                                secondInvestment.getInitialAmount()),

                                () -> assertEquals(Calendar.MONTH,
                                                secondInvestmentDate.getMonthValue()),

                                () -> assertEquals(
                                                Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
                                                secondInvestmentDate.getDayOfMonth()),

                                () -> assertEquals(Calendar.getInstance().get(Calendar.YEAR),
                                                secondInvestmentDate.getYear()),

                                // Third Investment
                                () -> assertEquals(
                                                new BigDecimal("2000000.50")
                                                                .divide(new BigDecimal("100"))
                                                                .setScale(3, RoundingMode.DOWN),
                                                thirdInvestment.getInitialAmount()),

                                () -> assertEquals(Calendar.MONTH,
                                                thirdInvestmentDate.getMonthValue()),

                                () -> assertEquals(
                                                Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
                                                thirdInvestmentDate.getDayOfMonth()),

                                () -> assertEquals(Calendar.getInstance().get(Calendar.YEAR),
                                                thirdInvestmentDate.getYear()));
        }

        @Test
        @DisplayName("it should throw an exception if user id DTO is null")
        public void caseTwo() {
                int page = 1;
                int perPage = 5;

                Exception exception = Assertions.assertThrows(BadRequestException.class, () -> {
                        this.sut.listByUser(null, page, perPage);
                });

                Assertions.assertEquals("Invalid user id.", exception.getMessage());
        }

        @Test
        @DisplayName("it should throw an exception if user doesn't exists")
        public void caseThree() {
                int page = 1;
                int perPage = 5;

                Exception exception = Assertions.assertThrows(NotFoundException.class, () -> {
                        this.sut.listByUser(UUID.randomUUID(), page, perPage);
                });

                Assertions.assertEquals("User not found.", exception.getMessage());
        }

        public Investment investmentGenerator(UUID userId, BigDecimal value) {
                NewInvestmentDto investment = new NewInvestmentDto();
                investment.setAmount(value);

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
