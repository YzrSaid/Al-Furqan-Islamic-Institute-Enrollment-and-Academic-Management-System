package com.example.testingLogIn.PasswordResetPackage;

import com.example.testingLogIn.Models.AccountRegister;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class AccountConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @ManyToOne
    @JoinColumn(name = "account")
    private AccountRegister accountRegister;
    private LocalDateTime expiryDate;

    public AccountConfirmationToken(AccountRegister register, String token){
        accountRegister = register;
        this.token = token;
        expiryDate = LocalDateTime.now().plusDays(1);
    }
}
