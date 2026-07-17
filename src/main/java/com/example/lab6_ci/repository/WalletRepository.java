package com.example.lab6_ci.repository;

import com.example.lab6_ci.model.Wallet;

import java.util.Optional;

public interface WalletRepository {
    Wallet save(Wallet wallet);
    Optional<Wallet> findById(String id);
    boolean existsByOwnerEmail(String ownerEmail);
}
