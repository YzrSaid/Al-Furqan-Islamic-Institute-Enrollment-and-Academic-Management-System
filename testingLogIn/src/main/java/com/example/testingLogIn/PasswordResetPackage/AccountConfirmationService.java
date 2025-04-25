package com.example.testingLogIn.PasswordResetPackage;

import com.example.testingLogIn.Models.AccountRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountConfirmationService {
    @Autowired
    private AccountConfirmTokenRepo accountConfirmTokenRepo;
    @Autowired
    private EmailService emailService;

    public void registerAccountToken(AccountRegister account){
        AccountConfirmationToken accountToken = accountConfirmTokenRepo.save(new AccountConfirmationToken(account,TokenGenerator.generateToken()));
        emailService.sendAccountConfirmation(account.getUsername(),accountToken.getToken());
    }
}
