package com.example.testingLogIn.StatisticsModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SubjectsStatistics {
    private List<String> subjectNames;
    private List<Float> avgGrades;
}
