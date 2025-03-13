package com.example.testingLogIn.ModelDTO;

import com.example.testingLogIn.Enums.EnrollmentStatus;
import com.example.testingLogIn.Enums.Semester;
import com.example.testingLogIn.Models.Student;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author magno
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDTO {
    private int enrollmentId;
    private Student student;
    private String schoolYear;
    private Semester semester;
    private Integer preRequisiteId;
    private Integer gradeLevelToEnrollId;
    private String gradeLevelToEnroll;
    private String sectionToEnroll;
    private String remarks;
    private EnrollmentStatus enrollmentStatus;
    private boolean isQualified;
    private boolean isComplete;
    private boolean isNotDeleted;
    private String studentMiddleName;
}
