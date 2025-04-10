package com.example.testingLogIn.ModelDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StudentGradesPerSem {
    private String sectionSemester;
    private String gradeSection;
    private List<StudentSubjectGradeDTO> grades;
}
