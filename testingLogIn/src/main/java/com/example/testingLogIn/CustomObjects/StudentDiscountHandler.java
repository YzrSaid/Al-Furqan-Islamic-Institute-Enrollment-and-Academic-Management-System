package com.example.testingLogIn.CustomObjects;

import com.example.testingLogIn.AssociativeModels.StudentDiscount;
import com.example.testingLogIn.ModelDTO.StudentDiscountDTO;
import com.example.testingLogIn.Models.Discount;
import com.example.testingLogIn.Models.Student;
import lombok.Data;

import java.util.Optional;

@Data
public class StudentDiscountHandler {
    private StudentDiscount discount;
    private Student student;

    public StudentDiscountHandler(StudentDiscount discount, Student student) {
        this.discount = discount;
        this.student = student;
    }

    public StudentDiscountDTO DTOmapper(){
        return StudentDiscountDTO.builder()
                .connectionId(Optional.ofNullable(discount).map(StudentDiscount::getConnectionId).orElse(0))
                .discountName(Optional.ofNullable(discount).map(disc -> disc.getDiscount().getDiscountName()).orElse("N/A"))
                .studentId(Optional.ofNullable(student).map(Student::getStudentId).orElse(0))
                .studentName(Optional.ofNullable(student).map(Student::getFullName).orElse("N/A"))
                .gradeLevel(Optional.ofNullable(student.getCurrentGradeSection()).map(sec -> sec.getLevel().getLevelName()).orElse("N/A"))
                .isNotDeleted(Optional.ofNullable(discount).map(StudentDiscount::isNotDeleted).orElse(false))
                .build();
    }
}
