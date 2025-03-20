package com.example.testingLogIn.Models;

import com.example.testingLogIn.Enums.EnrollmentStatus;
import com.example.testingLogIn.ModelDTO.EnrollmentDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.util.Optional;

/**
 *
 * @author magno
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int enrollmentId;
    
    @ManyToOne
    @JoinColumn(name = "student")
    private Student student;
    
    private String remarks;
    
    @ManyToOne
    @JoinColumn(name = "semester")
    private SchoolYearSemester SYSemester;
    
    @ManyToOne
    @JoinColumn(name = "gradeToEnroll")
    private GradeLevel gradeLevelToEnroll;
    
    @ManyToOne
    @JoinColumn(name = "sectionToEnroll")
    private Section sectionToEnroll;
    
    @Enumerated(EnumType.STRING)
    private EnrollmentStatus enrollmentStatus;
    
    private boolean isQualified;
    private boolean isComplete;
    private boolean isNotDeleted;
    
    public EnrollmentDTO DTOmapper(boolean isComplete){
        Integer preReqId = gradeLevelToEnroll == null ? null : gradeLevelToEnroll.getPreRequisite() == null ? null :gradeLevelToEnroll.getPreRequisite().getLevelId();
        return EnrollmentDTO.builder()
                            .enrollmentId(enrollmentId)
                            .student(student.DTOmapper())
                            .schoolYear(SYSemester.getSchoolYear().getSchoolYear())
                            .semester(SYSemester.getSem())
                            .preRequisiteId(preReqId)
                            .gradeLevelToEnrollId(Optional.ofNullable(gradeLevelToEnroll).map(GradeLevel::getLevelId).orElse(null))
                            .gradeLevelToEnroll(Optional.ofNullable(gradeLevelToEnroll).map(GradeLevel::getLevelName).orElse(null))
                            .sectionToEnroll(Optional.ofNullable(sectionToEnroll).map(sec -> sec.getLevel().getLevelName()+"-"+sec.getSectionName()).orElse(null))
                            .enrollmentStatus(enrollmentStatus)
                            .studentMiddleName(student.getMiddleName())
                            .remarks(remarks)
                            .studentMiddleName(student.getMiddleName())
                            .isQualified(isQualified)
                            .isComplete(isComplete)
                            .isNotDeleted(isNotDeleted)
                            .build();
    }
    
}
