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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class GradeLevel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int levelId;
    private String levelName;
    private boolean isNotDeleted;
    private int duration;

    @ManyToOne
    @JoinColumn(name = "preRequisite", nullable = true)
    @JsonIgnore
    private GradeLevel preRequisite;

    @OneToMany(mappedBy = "gradeLevel")
    @JsonIgnore
    private List<Subject> subjects;

    
    public GradeLevelDTO mapperDTO(){
        int count = 0;
        for(Subject sub : subjects){
            if(sub.isNotDeleted())
                count++;
        }
        return GradeLevelDTO.builder()
                            .semDuration(duration)
                            .isNotDeleted(isNotDeleted)
                            .levelId(levelId)
                            .levelName(levelName)
                            .subjectsCount(count)
                            .preRequisite(preRequisite != null ? preRequisite.getLevelName() : "None")
                            .build();
    }
}
