package com.example.testingLogIn.CustomObjects;

import com.example.testingLogIn.Models.Enrollment;
import com.example.testingLogIn.Models.Student;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class EnrollmentHandler {
    private Enrollment enrollment;
    private Student student;

    public EnrollmentHandler(Enrollment enrollment, Student student) {
        this.enrollment = enrollment;
        this.student = student;
    }
}
