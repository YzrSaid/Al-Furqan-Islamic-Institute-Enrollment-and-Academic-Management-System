package com.example.testingLogIn.CustomObjects;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FeeReports {

    private String feeName;
    private double totalPaid;
}
