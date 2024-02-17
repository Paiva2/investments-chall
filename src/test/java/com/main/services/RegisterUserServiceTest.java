package com.main.services;

import org.junit.jupiter.api.*;

import com.main.repositories.*;
import com.main.backendtest.dtos.request.RegisterUserDto;
import com.main.backendtest.entities.User;
import com.main.backendtest.exceptions.BadRequestException;
import com.main.backendtest.exceptions.ConflictException;
import com.main.backendtest.services.UserService;
import com.main.backendtest.services.WalletService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class RegisterUserServiceTest {
    private BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

    private UserRepositoryTest userRepositoryTest;

    private WalletRepositoryTest walletRepositoryTest;

    private WalletService walletService;

    private UserService sut;

    @BeforeEach
    public void setup() {
        this.userRepositoryTest = new UserRepositoryTest();
        this.walletRepositoryTest = new WalletRepositoryTest();
        this.walletService = new WalletService(walletRepositoryTest);

        this.sut = new UserService(userRepositoryTest, walletService);
    }

    @Test
    @DisplayName("should register an new user")
    public void caseOne() {
        RegisterUserDto newUser = new RegisterUserDto();
        newUser.setEmail("johndoe@test.com");
        newUser.setName("John Doe");
        newUser.setPassword("123456");

        User userCreated = this.sut.register(newUser);

        boolean checkHashedPassword =
                this.bcrypt.matches(newUser.getPassword(), userCreated.getPasswordHash());

        Assertions.assertNotNull(userCreated.getId());
        Assertions.assertTrue(checkHashedPassword);
        Assertions.assertEquals(newUser.getEmail(), userCreated.getEmail());
        Assertions.assertEquals(newUser.getName(), userCreated.getName());
    }

    @Test
    @DisplayName("should throw an exception if DTO is null")
    public void caseTwo() {
        RegisterUserDto newUser = null;

        Exception exception = Assertions.assertThrows(BadRequestException.class, () -> {
            this.sut.register(newUser);
        });

        Assertions.assertEquals(exception.getMessage(), "New user can't be null.");
    }


    @Test
    @DisplayName("should throw an exception if DTO password has less than 6 characters")
    public void caseThree() {
        RegisterUserDto newUser = new RegisterUserDto();
        newUser.setEmail("johndoe@test.com");
        newUser.setName("John Doe");
        newUser.setPassword("12345");

        Exception exception = Assertions.assertThrows(BadRequestException.class, () -> {
            this.sut.register(newUser);
        });

        Assertions.assertEquals(exception.getMessage(),
                "Password must have at least 6 characters.");
    }

    @Test
    @DisplayName("should throw an exception if DTO e-mail is null")
    public void caseFour() {
        RegisterUserDto newUser = new RegisterUserDto();
        newUser.setEmail(null);
        newUser.setName("John Doe");
        newUser.setPassword("123456");

        Exception exception = Assertions.assertThrows(BadRequestException.class, () -> {
            this.sut.register(newUser);
        });

        Assertions.assertEquals(exception.getMessage(), "E-mail can't be null.");
    }


    @Test
    @DisplayName("should throw an exception if DTO name is null")
    public void caseFive() {
        RegisterUserDto newUser = new RegisterUserDto();
        newUser.setEmail("johndoe@test.com");
        newUser.setName(null);
        newUser.setPassword("123456");

        Exception exception = Assertions.assertThrows(BadRequestException.class, () -> {
            this.sut.register(newUser);
        });

        Assertions.assertEquals(exception.getMessage(), "Name can't be null.");
    }


    @Test
    @DisplayName("should throw an exception if DTO e-mail already exists")
    public void caseSix() {
        RegisterUserDto newUser = new RegisterUserDto();
        newUser.setEmail("johndoe@test.com");
        newUser.setName("John Doe");
        newUser.setPassword("123456");

        this.sut.register(newUser);

        Exception exception = Assertions.assertThrows(ConflictException.class, () -> {
            this.sut.register(newUser);
        });

        Assertions.assertEquals(exception.getMessage(), "E-mail already exists.");
    }
}
