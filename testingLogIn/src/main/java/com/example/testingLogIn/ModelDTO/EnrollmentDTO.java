package com.example.testingLogIn.ModelDTO;

import com.example.testingLogIn.Enums.EnrollmentStatus;
import com.example.testingLogIn.Enums.Semester;
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
    private boolean isNew;
    private int studentId;
    private String studentFirstName;
    private String studentLastName;
    private String currentGradeLevel;
    private String schoolYear;
    private Semester semester;
    private String gradeLevelToEnroll;
    private String sectionToEnroll;
    private String remarks;
    private EnrollmentStatus enrollmentStatus;
    private boolean isQualified;
    private boolean isComplete;
    private boolean isNotDeleted;
    private String studentMiddleName;
}
