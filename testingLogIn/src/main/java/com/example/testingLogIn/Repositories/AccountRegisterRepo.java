package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.Models.AccountRegister;
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
}
