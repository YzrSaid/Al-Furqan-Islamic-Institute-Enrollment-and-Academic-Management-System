package com.example.testingLogIn.Models;

import com.example.testingLogIn.ModelDTO.SubjectDTO;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
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
}
