package com.example.testingLogIn.AssociativeModels;

import com.example.testingLogIn.Models.AddOns;
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


    @ManyToOne
    @JoinColumn(name = "addOn")
    private AddOns addOns;

    private double amount;
    private double discount;

    @ManyToOne
    @JoinColumn(name = "sem")
    private SchoolYearSemester sem;

    public static StudentFeesList build(RequiredFees fee, SchoolYearSemester sem, Student student, double discountedAmount){
        return builder()
                .fee(fee)
                .sem(sem)
                .student(student)
                .amount(discountedAmount)
                .build();
    }
}
