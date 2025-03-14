package com.example.testingLogIn.CustomObjects;

import com.example.testingLogIn.ModelDTO.StudentDTO;
import com.example.testingLogIn.Models.RequiredFees;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Builder
public class StudentPaymentForm {
    private StudentDTO student;
    private double totalFee;
    private Map<Double,RequiredFees> feesAndBalance = new HashMap<>();

}
