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
    private String studentFirstName;
    private String studentLastName;
    private String schoolYear;
    private Semester semester;
    private String gradeLevel;
    private String sectionName;
    private EnrollmentStatus enrollmentStatus;
    private boolean isNotDeleted;
}
