package com.example.testingLogIn.ModelDTO;

import com.example.testingLogIn.Enums.EnrollmentStatus;
import com.example.testingLogIn.Enums.Semester;
import com.example.testingLogIn.Models.Enrollment;
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
    private StudentDTO student;
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

    public EnrollmentDTO(EnrollmentDTO e, StudentDTO student){
        this.enrollmentStatus = EnrollmentStatus.NOT_REGISTERED;
        this.isNotDeleted = true;
        if(e!=null){
            this.enrollmentId = e.getEnrollmentId();
            this.schoolYear = e.getSchoolYear();
            this.semester = e.getSemester();
            this.preRequisiteId = e.getPreRequisiteId();
            this.gradeLevelToEnrollId = e.getGradeLevelToEnrollId();
            this.gradeLevelToEnroll = e.getGradeLevelToEnroll();
            this.sectionToEnroll = e.getSectionToEnroll();
            this.remarks = e.getRemarks();
            this.enrollmentStatus = e.getEnrollmentStatus();
            this.isQualified = e.isQualified();
            this.isComplete = e.isComplete();
            this.isNotDeleted = e.isNotDeleted();
        }
        this.student=student;
    }
}
