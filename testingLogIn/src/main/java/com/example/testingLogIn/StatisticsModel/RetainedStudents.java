package com.example.testingLogIn.StatisticsModel;

import com.example.testingLogIn.Models.GradeLevel;
import com.example.testingLogIn.Models.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity
public class RetainedStudents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "student")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "gradeLevel")
    private GradeLevel gradeLevel;
}
