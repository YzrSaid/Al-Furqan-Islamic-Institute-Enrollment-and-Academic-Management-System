package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.Models.PaymentTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentTransactionRepo extends JpaRepository<PaymentTransaction,String> {
    @Query("SELECT COUNT(*) from PaymentTransaction pt")
    Integer countTotalTransactions();

    @Query( "SELECT pt FROM PaymentTransaction pt " +
            "JOIN pt.student stud " +
            "WHERE pt.isNotVoided = :isVoided " +
            "AND (LOWER(stud.fullName) LIKE CONCAT('%',LOWER(:search),'%') " +
            "OR stud.studentDisplayId LIKE CONCAT('%',LOWER(:search),'%'))")
    Page<PaymentTransaction> getTransactionsIsVoided(@Param("search")String search,
                                                      @Param("isVoided") boolean isVoided,
                                                                        Pageable pageable);
}
