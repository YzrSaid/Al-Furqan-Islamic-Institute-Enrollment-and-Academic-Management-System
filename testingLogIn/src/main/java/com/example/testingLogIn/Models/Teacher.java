package com.example.testingLogIn.Models;

import com.example.testingLogIn.Enums.Gender;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserModel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Teacher{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int count;

    @ManyToOne
    @JoinColumn(name="staffId")
    private UserModel user;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    
    private String address;
    private LocalDate birthdate;
    private String contactNum;
    private boolean isNotDeleted;
}
