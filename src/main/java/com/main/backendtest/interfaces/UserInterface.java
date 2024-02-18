package com.main.backendtest.interfaces;

import java.util.Optional;
import java.util.UUID;

import com.main.backendtest.entities.User;

public interface UserInterface {
    User save(User user);

    Optional<User> findByEmail(String findByEmail);

    Optional<User> findById(UUID userId);
}
