package com.example.testingLogIn.CustomObjects;

import com.example.testingLogIn.Models.GradeLevel;
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
}
