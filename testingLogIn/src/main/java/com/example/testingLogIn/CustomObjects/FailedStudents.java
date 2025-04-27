package com.example.testingLogIn.CustomObjects;

import com.example.testingLogIn.Models.GradeLevel;
import com.example.testingLogIn.Models.Section;
import com.example.testingLogIn.Models.Student;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FailedStudents {

    private Student student;
    private GradeLevel gradeLevel;
    private Section section;
    private double average;

    public FailedStudents(Student student, Section section, double average) {
        this.student = student;
        this.section = section;
        this.average = average;
    }
}
