package com.example.testingLogIn.CustomObjects;

import com.example.testingLogIn.Models.GradeLevel;
import com.example.testingLogIn.Models.Section;
import com.example.testingLogIn.Models.Student;
import lombok.Data;

@Data
public class StudentHandler {

    private Student student;
    private Section sectionName;

    public StudentHandler(Student student, Section sectionName) {
        this.student = student;
        this.sectionName = sectionName;
    }
}
