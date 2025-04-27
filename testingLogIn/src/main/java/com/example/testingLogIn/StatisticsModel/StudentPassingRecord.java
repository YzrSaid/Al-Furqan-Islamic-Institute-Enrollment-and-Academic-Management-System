package com.example.testingLogIn.StatisticsModel;

import com.example.testingLogIn.Models.GradeLevel;
import com.example.testingLogIn.Models.SchoolYearSemester;
import com.example.testingLogIn.Models.Section;
import com.example.testingLogIn.Models.Student;
import com.example.testingLogIn.Services.NonModelServices;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Data
public class StudentPassingRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "student")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "section")
    private Section section;

    @ManyToOne
    @JoinColumn(name = "sem")
    private SchoolYearSemester sem;
    private double average;
    private boolean didPassed;

    public StudentPassingRecord(boolean didPassed, SchoolYearSemester sem, Section section, Student student, double average) {
        this.didPassed = didPassed;
        this.sem = sem;
        this.section = section;
        this.student = student;
        this.average = NonModelServices.adjustDecimal(average);
    }
}
