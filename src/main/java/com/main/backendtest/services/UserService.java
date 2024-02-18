package com.main.backendtest.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.main.backendtest.dtos.request.AuthUserDto;
import com.main.backendtest.dtos.request.RegisterUserDto;
import com.main.backendtest.entities.User;
import com.main.backendtest.exceptions.BadRequestException;
import com.main.backendtest.exceptions.ConflictException;
import com.main.backendtest.exceptions.ForbiddenException;
import com.main.backendtest.exceptions.NotFoundException;
import com.main.backendtest.interfaces.UserInterface;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.transaction.Transactional;

@Service
public class UserService {
    private BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

    private final UserInterface userRepository;

    private final WalletService walletService;

    public UserService(UserInterface userRepository, WalletService walletService) {
        this.userRepository = userRepository;
        this.walletService = walletService;
    }

    @Transactional
    public User register(RegisterUserDto dto) {
        if (dto == null) {
            throw new BadRequestException("New user can't be null.");
        }

        if (dto.getPassword().length() < 6) {
            throw new BadRequestException("Password must have at least 6 characters.");
        }

        if (dto.getEmail() == null) {
            throw new BadRequestException("E-mail can't be null.");
        }

        if (dto.getName() == null) {
            throw new BadRequestException("Name can't be null.");
        }

        Optional<User> doesEmailAlreadyExists = this.userRepository.findByEmail(dto.getEmail());

        if (doesEmailAlreadyExists.isPresent()) {
            throw new ConflictException("E-mail already exists.");
        }

        String hashPassword = this.bcrypt.encode(dto.getPassword());

        User newUser = new User();
        newUser.setEmail(dto.getEmail());
        newUser.setName(dto.getName());
        newUser.setPasswordHash(hashPassword);

        User userCreation = this.userRepository.save(newUser);

        if (userCreation != null) {
            this.walletService.create(userCreation);
        }

        return userCreation;
    }

    public User auth(AuthUserDto dto) {
        if (dto == null) {
            throw new BadRequestException("Invalid dto.");
        }

        if (dto.getPassword().length() < 6) {
            throw new BadRequestException("Password must have at least 6 characters.");
        }

        Optional<User> doesUserExists = this.userRepository.findByEmail(dto.getEmail());

        if (doesUserExists.isEmpty()) {
            throw new NotFoundException("User not found.");
        }

        boolean doesPasswordMatches =
                this.bcrypt.matches(dto.getPassword(), doesUserExists.get().getPasswordHash());

        if (!doesPasswordMatches) {
            throw new ForbiddenException("Wrong credentials.");
        }

        return doesUserExists.get();
    }
}
