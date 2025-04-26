package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.Enums.RegistrationStatus;
import com.example.testingLogIn.Models.AccountRegister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRegisterRepo extends JpaRepository<AccountRegister,Integer> {

    @Query("""
           SELECT ar FROM AccountRegister ar
           WHERE LOWER(ar.username) = :userName
           """)
    Optional<AccountRegister> findByUserName(String userName);

    @Query("""
            SELECT ar FROM AccountRegister ar
            WHERE (:status IS NULL OR ar.status = :status)
            AND LOWER(
                CONCAT(
                    TRIM(ar.firstname),
                    ' ',
                    CASE WHEN ar.middlename IS NULL OR ar.middlename = '' THEN '' ELSE CONCAT(TRIM(ar.middlename), ' ') END,
                    TRIM(ar.lastname))
                ) LIKE :search
            """)
    Page<AccountRegister> accountRegisterPage(
            String search,
            RegistrationStatus status,
            Pageable pageable
    );
}
