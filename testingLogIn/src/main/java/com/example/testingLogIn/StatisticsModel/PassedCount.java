package com.example.testingLogIn.StatisticsModel;

import com.example.testingLogIn.Models.SchoolYearSemester;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
public class PassedCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "sem")
    private SchoolYearSemester sem;

    private int count;

    public PassedCount(int count, SchoolYearSemester sem) {
        this.sem = sem;
        this.count = count;
    }
}
