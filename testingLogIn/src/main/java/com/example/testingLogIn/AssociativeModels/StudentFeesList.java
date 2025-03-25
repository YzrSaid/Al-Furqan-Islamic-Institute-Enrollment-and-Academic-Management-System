package com.example.testingLogIn.AssociativeModels;

import com.example.testingLogIn.Models.RequiredFees;
import com.example.testingLogIn.Models.SchoolYearSemester;
import com.example.testingLogIn.Models.Student;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
public class StudentFeesList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int number;

    @ManyToOne
    @JoinColumn(name="student")
    private Student student;

    @ManyToOne
    @JoinColumn(name="fee")
    private RequiredFees fee;

    private double amount;

    @ManyToOne
    @JoinColumn(name = "sem")
    private SchoolYearSemester sem;
}
