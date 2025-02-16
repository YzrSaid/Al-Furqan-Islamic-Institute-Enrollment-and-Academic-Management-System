package com.example.testingLogIn.Models;

import com.example.testingLogIn.Enums.Gender;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserModel;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Teacher{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int count;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="teacherId")
    private UserModel user;

    private String address;
    private LocalDate birthdate;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String contactNum;

    @Override
    public String toString() {
        return "Teacher{" +
                "count=" + count +
                ", staffid=" + user.toString() +
                ", address='" + address + '\'' +
                ", birthdate=" + birthdate +
                ", gender=" + gender +
                ", contactNum='" + contactNum + '\'' +
                '}';
    }
}
