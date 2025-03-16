package com.example.testingLogIn.Models;

import com.example.testingLogIn.ModelDTO.GradeLevelDTO;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class GradeLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int levelId;
    private String levelName;
    private boolean isNotDeleted;

    @ManyToOne
    @JoinColumn(name = "preRequisite", nullable = true)
    @JsonIgnore
    private GradeLevel preRequisite;
    
    public GradeLevelDTO mapperDTO(){
        try{
        }catch(Exception e){
            
        }
        return GradeLevelDTO.builder()
                            .isNotDeleted(isNotDeleted)
                            .levelId(levelId)
                            .levelName(levelName)
                            .preRequisite(preRequisite != null ? preRequisite.getLevelName() : "None")
                            .build();
    }
}
