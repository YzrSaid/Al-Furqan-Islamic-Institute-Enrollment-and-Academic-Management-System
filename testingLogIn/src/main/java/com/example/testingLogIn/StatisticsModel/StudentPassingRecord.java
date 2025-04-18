package com.example.testingLogIn.StatisticsModel;

import com.example.testingLogIn.Models.GradeLevel;
import com.example.testingLogIn.Models.SchoolYearSemester;
import com.example.testingLogIn.Models.Student;
import jakarta.persistence.*;

public class StudentPassingRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "student")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "gradeLevel")
    private GradeLevel gradeLevel;

    @ManyToOne
    @JoinColumn(name = "sem")
    private SchoolYearSemester sem;

    private boolean didPassed;

    public StudentPassingRecord(boolean didPassed, SchoolYearSemester sem, GradeLevel gradeLevel, Student student) {
        this.didPassed = didPassed;
        this.sem = sem;
        this.gradeLevel = gradeLevel;
        this.student = student;
    }
}
