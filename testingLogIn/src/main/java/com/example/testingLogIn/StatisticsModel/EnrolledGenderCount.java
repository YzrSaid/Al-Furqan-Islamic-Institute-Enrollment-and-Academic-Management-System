package com.example.testingLogIn.StatisticsModel;

import com.example.testingLogIn.Enums.Gender;
import com.example.testingLogIn.Models.SchoolYearSemester;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class EnrolledGenderCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private Gender gender;
    @ManyToOne
    @JoinColumn(name = "sem")
    private SchoolYearSemester sem;
    private int count;

    public EnrolledGenderCount(Gender gender, SchoolYearSemester sem,int count) {
        this.gender = gender;
        this.count = count;
        this.sem = sem;
    }
}
