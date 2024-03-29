package com.main.repositories;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

import com.main.backendtest.entities.User;
import com.main.backendtest.interfaces.UserInterface;


public class UserRepositoryTest implements UserInterface {
    protected List<User> users = new ArrayList<>();

    @Override
    public User save(User user) {
        User handleUser = null;

        Optional<User> doesUserExists =
                this.users.stream().filter(users -> users.getId().equals(user.getId())).findFirst();

        if (doesUserExists.isEmpty()) {
            // In some tests we need to pass userId before send user
            // entity to repository
            if (user.getId() == null) {
                user.setId(UUID.randomUUID());
            }

            this.users.add(user);

            handleUser = user;
        } else {
            int getCurrentUserIdx = this.users.indexOf(doesUserExists.get());
            this.users.set(getCurrentUserIdx, user);

            handleUser = this.users.get(getCurrentUserIdx);
        }

        return handleUser;
    }

    @Override
    public Optional<User> findByEmail(String findByEmail) {
        return this.users.stream()
                .filter(user -> user.getEmail().hashCode() == findByEmail.hashCode()).findFirst();
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return this.users.stream().filter(user -> user.getId().hashCode() == userId.hashCode())
                .findFirst();
    }
}
