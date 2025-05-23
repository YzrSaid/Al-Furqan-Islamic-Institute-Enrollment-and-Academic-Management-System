package com.example.testingLogIn.ModelDTO;

import lombok.Builder;
import lombok.Data;

/**
 *
 * @author magno
 */
@Data
@Builder
public class StudentSubjectGradeDTO {
    private int studGradeId;
    private String studentId;
    private String studentFirstName;
    private String studentLastName;
    private String studentMiddleName;
    private String studentFullName;
    private String gradeAndSection;
    private String subjectName;
    private Integer subjectGrade;
}
