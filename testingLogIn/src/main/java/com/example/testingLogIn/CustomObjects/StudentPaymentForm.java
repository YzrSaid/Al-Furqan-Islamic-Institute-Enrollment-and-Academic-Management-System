package com.example.testingLogIn.CustomObjects;

import com.example.testingLogIn.ModelDTO.StudentDTO;
import com.example.testingLogIn.Models.RequiredFees;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
public class StudentPaymentForm {
    private StudentDTO student;
    private double totalFee;
    private List<FeesAndBalance> feesAndBalance;

    public static StudentPaymentForm preBuilt(StudentDTO student){
        return StudentPaymentForm.builder()
                .totalFee(0.0d)
                .student(student)
                .feesAndBalance(new ArrayList<>())
                .build();
    }
}
