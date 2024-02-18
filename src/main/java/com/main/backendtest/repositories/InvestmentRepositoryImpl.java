package com.main.backendtest.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.main.backendtest.entities.Investment;
import com.main.backendtest.interfaces.InvestmentInterface;

public interface InvestmentRepositoryImpl
                extends InvestmentInterface, JpaRepository<Investment, UUID> {
}
