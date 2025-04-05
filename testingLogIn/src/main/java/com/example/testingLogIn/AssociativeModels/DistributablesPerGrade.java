package com.example.testingLogIn.AssociativeModels;

import com.example.testingLogIn.Models.Distributable;
import com.example.testingLogIn.Models.GradeLevel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class DistributablesPerGrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "item",nullable = false)
    @JsonIgnore
    private Distributable item;

    @ManyToOne
    @JoinColumn(name = "gradeLevel",nullable = false)
    private GradeLevel gradeLevel;

    private boolean isNotDeleted;

    public DistributablesPerGrade(Distributable item, GradeLevel gradeLevel){
        this.item = item;
        this.gradeLevel = gradeLevel;
        isNotDeleted = true;
    }
}
