package com.example.testingLogIn.Models;

import com.example.testingLogIn.ModelDTO.SubjectDTO;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Entity
public class Subject {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int subjectNumber;
    
    @ManyToOne
    @JoinColumn(name = "gradeLevel")
    private GradeLevel gradeLevel;
    private String subjectName;
    private boolean isNotDeleted;
    private boolean isCurrentlyActive;
    
    public SubjectDTO mapper(){
        return SubjectDTO.builder()
                          .subjectNumber(subjectNumber)
                          .gradeLevel(gradeLevel.getLevelName())
                          .subjectName(subjectName)
                          .isNotDeleted(isNotDeleted)
                          .willApplyNow(isCurrentlyActive)
                          .build();
    }
}
