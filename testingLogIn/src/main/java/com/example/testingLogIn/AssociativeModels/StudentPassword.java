package com.example.testingLogIn.AssociativeModels;

import com.example.testingLogIn.Models.Student;
import com.example.testingLogIn.WebsiteSecurityConfiguration.StudentPasswordRepo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
public class StudentPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int connectionId;
    @OneToOne
    @JoinColumn(name = "student")
    private Student student;
    private String password;

    public StudentPassword(Student student, String password){
        this.student = student;
        this.password = password;
    }
}
