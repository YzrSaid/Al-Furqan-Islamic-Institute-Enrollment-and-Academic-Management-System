package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.Models.AccountRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRegisterRepo extends JpaRepository<AccountRegister,Integer> {
    
}
