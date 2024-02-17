package com.main.backendtest.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.main.backendtest.entities.Wallet;
import com.main.backendtest.interfaces.WalletInterface;

public interface WalletRepositoryImpl extends WalletInterface, JpaRepository<Wallet, UUID> {
}
