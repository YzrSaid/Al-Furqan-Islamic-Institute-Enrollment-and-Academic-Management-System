package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.Models.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentTransactionRepo extends JpaRepository<PaymentTransaction,String> {

    @Query("SELECT COUNT(*) from PaymentTransaction pt")
    Integer countTotalTransactions();
}
