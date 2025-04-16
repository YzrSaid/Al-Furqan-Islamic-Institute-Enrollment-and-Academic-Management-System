package com.example.testingLogIn.StatisticsModel;

import com.example.testingLogIn.Models.SchoolYearSemester;
import com.example.testingLogIn.Models.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class GraduateStudents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "student")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "sem")
    private SchoolYearSemester sem;

    public GraduateStudents(SchoolYearSemester sem, Student student) {
        this.sem = sem;
        this.student = student;
    }
}
