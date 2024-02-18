package com.main.services.user;

import org.junit.jupiter.api.*;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.main.backendtest.dtos.request.user.AuthUserDto;
import com.main.backendtest.dtos.request.user.RegisterUserDto;
import com.main.backendtest.entities.User;
import com.main.backendtest.exceptions.BadRequestException;
import com.main.backendtest.exceptions.ForbiddenException;
import com.main.backendtest.exceptions.NotFoundException;
import com.main.backendtest.services.UserService;
import com.main.backendtest.services.WalletService;
import com.main.repositories.UserRepositoryTest;
import com.main.repositories.WalletRepositoryTest;

@ActiveProfiles("test")
public class AuthUserServiceTest {
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

        this.userGenerator();
    }

    @Test
    @DisplayName("should auth an user")
    public void caseOne() {
        AuthUserDto user = new AuthUserDto();
        user.setEmail("johndoe@test.com");
        user.setPassword("123456");

        User authUser = this.sut.auth(user);

        boolean arePasswordsEquals =
                this.bcrypt.matches(user.getPassword(), authUser.getPasswordHash());

        Assertions.assertNotNull(authUser);
        Assertions.assertTrue(arePasswordsEquals);
        Assertions.assertEquals(user.getEmail(), authUser.getEmail());
    }

    @Test
    @DisplayName("should throw exception if DTO is null")
    public void caseTwo() {
        AuthUserDto user = null;

        Exception exception = Assertions.assertThrows(BadRequestException.class, () -> {
            this.sut.auth(user);
        });

        Assertions.assertEquals("Invalid dto.", exception.getMessage());
    }

    @Test
    @DisplayName("should throw an exception if password has less than 6 characters")
    public void caseThree() {
        AuthUserDto user = new AuthUserDto();
        user.setEmail("johndoe@test.com");
        user.setPassword("12345");

        Exception exception = Assertions.assertThrows(BadRequestException.class, () -> {
            this.sut.auth(user);
        });

        Assertions.assertEquals("Password must have at least 6 characters.",
                exception.getMessage());
    }

    @Test
    @DisplayName("should throw an exception if user doesn't exists")
    public void caseFour() {
        AuthUserDto user = new AuthUserDto();
        user.setEmail("inexistent@test.com");
        user.setPassword("123456");

        Exception exception = Assertions.assertThrows(NotFoundException.class, () -> {
            this.sut.auth(user);
        });

        Assertions.assertEquals("User not found.", exception.getMessage());
    }

    @Test
    @DisplayName("should throw an exception if credentials are wrong")
    public void caseFive() {
        AuthUserDto user = new AuthUserDto();
        user.setEmail("johndoe@test.com");
        user.setPassword("wrongpass");

        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            this.sut.auth(user);
        });

        Assertions.assertEquals("Wrong credentials.", exception.getMessage());
    }

    private User userGenerator() {
        RegisterUserDto newUser = new RegisterUserDto();
        newUser.setEmail("johndoe@test.com");
        newUser.setName("John Doe");
        newUser.setPassword("123456");

        return this.sut.register(newUser);
    }
}
