package com.example.testingLogIn.Services;

import com.example.testingLogIn.Models.AccountRegister;
import com.example.testingLogIn.Repositories.AccountRegisterRepo;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountRegisterServices {

    @Autowired
    AccountRegisterRepo accountRegisterRepo;

    public AccountRegister getAccount(int id){
        return accountRegisterRepo.findById(id).orElse(null);
    }
    
    public boolean registerAccount(AccountRegister accountRegister){
        accountRegisterRepo.save(accountRegister);
        return true;
    }
    
    public boolean deleteAccountRegistration(int id){
        AccountRegister todelete = getAccount(id);
        if(todelete == null)
            return false;
        else{
            todelete.setNotDeleted(false);
            accountRegisterRepo.save(todelete);
            return true;
        }
    }
    
    public List<AccountRegister> getAccounts(String status){
        return accountRegisterRepo.findAll().stream()
                                  .filter(account -> account.getStatus().toString().equalsIgnoreCase(status))
                                  .collect(Collectors.toList());
    }
}
