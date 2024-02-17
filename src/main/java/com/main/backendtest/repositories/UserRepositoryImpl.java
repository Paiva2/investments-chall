package com.main.backendtest.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.main.backendtest.entities.User;
import com.main.backendtest.interfaces.UserInterface;

public interface UserRepositoryImpl extends UserInterface, JpaRepository<User, UUID> {
}
