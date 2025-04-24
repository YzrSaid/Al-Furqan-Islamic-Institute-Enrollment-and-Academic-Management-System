package com.example.testingLogIn.ModelDTO;

import com.example.testingLogIn.Models.GradeLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class StudentDiscountDTO {
    private String studentName;
    private int studentId;
    private String gradeLevel;
    private String discountName;
    private int connectionId;
    private boolean isNotDeleted;
}
