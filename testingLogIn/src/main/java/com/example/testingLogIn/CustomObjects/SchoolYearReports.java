package com.example.testingLogIn.CustomObjects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SchoolYearReports {

    private List<FeeReports> feeReports;
    private List<GradeLevelEnrolledReports> totalEnrolled;

    public SchoolYearReports(List<FeeReports> feeReports,List<GradeLevelEnrolledReports> totalEnrolled) {
        this.feeReports = feeReports;
        this.totalEnrolled = totalEnrolled;
    }
}
