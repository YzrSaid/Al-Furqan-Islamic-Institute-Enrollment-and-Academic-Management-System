package com.example.testingLogIn.StatisticsModel;

import com.example.testingLogIn.Models.GradeLevel;
import com.example.testingLogIn.Models.SchoolYearSemester;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
public class GradeLevelRetainedCount {

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

    public GradeLevelRetainedCount(SchoolYearSemester sem, GradeLevel gradeLevel, int count) {
        this.sem = sem;
        this.gradeLevel = gradeLevel;
        this.count = count;
    }
}
