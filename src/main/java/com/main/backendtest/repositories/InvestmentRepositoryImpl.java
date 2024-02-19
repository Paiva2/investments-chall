package com.main.backendtest.repositories;

import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.main.backendtest.entities.Investment;
import com.main.backendtest.interfaces.InvestmentInterface;

public interface InvestmentRepositoryImpl
        extends InvestmentInterface, JpaRepository<Investment, UUID> {

    @Query("SELECT i FROM tb_investments i WHERE EXTRACT(DAY FROM i.createdAt) = EXTRACT(DAY FROM CURRENT_DATE)")
    List<Investment> getAllCreatedToday();
}
