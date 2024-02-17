package com.main.backendtest.services;

import org.springframework.stereotype.Service;

import com.main.backendtest.entities.User;
import com.main.backendtest.entities.Wallet;
import com.main.backendtest.exceptions.BadRequestException;
import com.main.backendtest.interfaces.WalletInterface;

@Service
public class WalletService {
    private final WalletInterface walletRepository;

    public WalletService(WalletInterface walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet create(User walletOwner) {
        if (walletOwner == null) {
            throw new BadRequestException("Wallet owner can't be null.");
        }

        Wallet newWallet = new Wallet();
        newWallet.setUser(walletOwner);

        return this.walletRepository.save(newWallet);
    }
}
