package com.example.testingLogIn.ModelDTO;

import com.example.testingLogIn.Models.GradeLevel;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class DistributablePerStudentDTO {

    private int distId;
    private StudentDTO student;
    private GradeLevel gradeLevel;
    private String itemName;
    private boolean isReceived;
    private LocalDate dateReceived;
}
