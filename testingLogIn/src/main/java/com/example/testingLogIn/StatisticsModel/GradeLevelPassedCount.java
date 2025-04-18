package com.example.testingLogIn.StatisticsModel;

import com.example.testingLogIn.Models.GradeLevel;
import com.example.testingLogIn.Models.SchoolYearSemester;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
public class GradeLevelPassedCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int count;
    @ManyToOne
    @JoinColumn(name = "sem")
    private SchoolYearSemester sem;

    @ManyToOne
    @JoinColumn(name = "gradeLevel")
    private GradeLevel gradeLevel;

    public GradeLevelPassedCount(GradeLevel gradeLevel, SchoolYearSemester sem, int count) {
        this.gradeLevel = gradeLevel;
        this.sem = sem;
        this.count = count;
    }
}
