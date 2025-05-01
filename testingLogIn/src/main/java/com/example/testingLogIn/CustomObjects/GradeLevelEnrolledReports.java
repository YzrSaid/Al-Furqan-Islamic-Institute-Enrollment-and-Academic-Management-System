package com.example.testingLogIn.CustomObjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class GradeLevelEnrolledReports {
    private String levelName;
    private int totalEnrolled;
}
