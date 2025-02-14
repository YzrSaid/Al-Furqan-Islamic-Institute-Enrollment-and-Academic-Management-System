package com.example.testingLogIn.Services;

import com.example.testingLogIn.Models.AccountRegister;
import com.example.testingLogIn.Repositories.AccountRegisterRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountRegisterServices {

    @Autowired
    AccountRegisterRepo accountRegisterRepo;

    public boolean registerAccount(AccountRegister accountRegister){
        accountRegisterRepo.save(accountRegister);
        return true;
    }


}
