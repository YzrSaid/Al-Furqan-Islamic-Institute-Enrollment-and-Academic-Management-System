package com.example.testingLogIn.Services;

import com.example.testingLogIn.CustomObjects.PagedResponse;
import com.example.testingLogIn.Enums.RegistrationStatus;
import com.example.testingLogIn.Models.AccountRegister;
import com.example.testingLogIn.PasswordResetPackage.AccountConfirmationService;
import com.example.testingLogIn.Repositories.AccountRegisterRepo;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class AccountRegisterServices {

    @Autowired
    AccountRegisterRepo accountRegisterRepo;
    @Autowired
    AccountConfirmationService accountConfirmation;

    public AccountRegister getAccount(int id){
        return accountRegisterRepo.findById(id).orElse(null);
    }
    
    public boolean registerAccount(AccountRegister accountRegister){
        AccountRegister regAccount = accountRegisterRepo.findByUserName(accountRegister.getUsername().toLowerCase()).orElse(null);
        if(regAccount == null || regAccount.getStatus() != RegistrationStatus.PENDING){
            AccountRegister account = accountRegisterRepo.save(accountRegister);
            accountConfirmation.registerAccountToken(account);
            return true;
        }else
            throw new IllegalArgumentException("Account email has a pending confirmation request");

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
    
    public PagedResponse getAccounts(String status, String search, int pageNo, int pageSize){
        RegistrationStatus regStatus = status.equalsIgnoreCase("all") ? null :
                                        status.equalsIgnoreCase("approved") ? RegistrationStatus.APPROVED :
                                        status.equalsIgnoreCase("rejected") ? RegistrationStatus.REJECTED :
                                                                                          RegistrationStatus.PENDING;
        search = NonModelServices.forLikeOperator(search);
        Page<AccountRegister> registerPage = accountRegisterRepo.accountRegisterPage(search,regStatus, PageRequest.of(pageNo - 1, pageSize));

        return PagedResponse.builder()
                .content(registerPage.getContent())
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalElements(registerPage.getTotalElements())
                .totalPages(registerPage.getTotalPages())
                .build();
    }
}
