package com.main.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;

import com.main.backendtest.entities.Wallet;
import com.main.backendtest.interfaces.WalletInterface;


public class WalletRepositoryTest implements WalletInterface {
    protected List<Wallet> wallets = new ArrayList<>();

    @Override
    public Wallet save(Wallet wallet) {
        Wallet handleWallet = null;

        Optional<Wallet> doesWalletExists = this.wallets.stream()
                .filter(wallets -> wallets.getId().equals(wallet.getId())).findFirst();

        if (doesWalletExists.isEmpty()) {
            wallet.setId(UUID.randomUUID());
            this.wallets.add(wallet);

            handleWallet = wallet;
        } else {
            int getCurrWalletIdx = this.wallets.indexOf(doesWalletExists.get());
            this.wallets.set(getCurrWalletIdx, wallet);

            handleWallet = this.wallets.get(getCurrWalletIdx);
        }

        return handleWallet;
    }

}
