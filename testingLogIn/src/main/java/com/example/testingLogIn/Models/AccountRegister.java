package com.example.testingLogIn.Models;

import com.example.testingLogIn.Enums.Gender;
import com.example.testingLogIn.Enums.RegistrationStatus;
import com.example.testingLogIn.Enums.Role;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Date;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AccountRegister {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private RegistrationStatus status;
    
    private String firstname;
    private String lastname;
    private String address;
    private String contactNumber;
    private LocalDate birthdate;
    
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private boolean isNotDeleted;
}
