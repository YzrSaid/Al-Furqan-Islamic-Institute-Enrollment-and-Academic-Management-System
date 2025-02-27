package com.example.testingLogIn.Models;

import com.example.testingLogIn.ModelDTO.GradeLevelDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GradeLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int levelId;
    private String levelName;
    private boolean isNotDeleted;
    
    @OneToOne
    @JoinColumn(name = "preRequiste", nullable = true)
    private GradeLevel preRequisite;
    
    public GradeLevelDTO mapperDTO(){
        return GradeLevelDTO.builder()
                            .isNotDeleted(isNotDeleted)
                            .levelId(levelId)
                            .levelName(levelName)
                            .preRequisite(preRequisite.getLevelName())
                            .build();
    }
}
