package com.main.backendtest.interfaces;

import java.util.Optional;

import com.main.backendtest.entities.User;

public interface UserInterface {
    User save(User user);

    Optional<User> findByEmail(String findByEmail);
}
