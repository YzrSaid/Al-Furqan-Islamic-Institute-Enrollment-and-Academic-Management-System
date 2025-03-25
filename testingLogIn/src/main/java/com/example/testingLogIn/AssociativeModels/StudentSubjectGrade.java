/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.testingLogIn.AssociativeModels;

import com.example.testingLogIn.ModelDTO.StudentSubjectGradeDTO;
import com.example.testingLogIn.Models.SchoolYearSemester;
import com.example.testingLogIn.Models.Section;
import com.example.testingLogIn.Models.Student;
import com.example.testingLogIn.Models.Subject;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

/**
 *
 * @author magno
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class StudentSubjectGrade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int numberId;
    
    @ManyToOne
    @JoinColumn(name = "student")
    private Student student;
    
    @ManyToOne
    @JoinColumn(name = "subject")
    private Subject subject;
    
    @ManyToOne
    @JoinColumn(name = "section")
    private Section section;
    
    @ManyToOne
    @JoinColumn(name = "semester")
    private SchoolYearSemester semester;
    private Integer subjectGrade;

    private boolean isDropped;
    
    public StudentSubjectGradeDTO DTOmapper(){
        return StudentSubjectGradeDTO.builder()
                                    .studGradeId(numberId)
                                    .studentId(student.getStudentDisplayId())
                                    .studentFirstName(student.getFirstName())
                                    .studentLastName(student.getLastName())
                                    .studentMiddleName(student.getLastName())
                                    .gradeAndSection(section.getLevel().getLevelName()+" - "+section.getSectionName())
                                    .subjectName(subject.getSubjectName())
                                    .subjectGrade(subjectGrade)
                                    .build();
    }
}
