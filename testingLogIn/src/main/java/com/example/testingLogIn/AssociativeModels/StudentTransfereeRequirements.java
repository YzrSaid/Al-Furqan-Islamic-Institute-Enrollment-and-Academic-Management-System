package com.example.testingLogIn.AssociativeModels;

import com.example.testingLogIn.Models.Student;
import com.example.testingLogIn.Models.TransfereeRequirements;
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
public class StudentTransfereeRequirements {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "student",nullable = false)
    @JsonIgnore
    private Student student;

    @ManyToOne
    @JoinColumn(name = "requirement")
    private TransfereeRequirements requirement;

    private boolean isNotDeleted;

}
