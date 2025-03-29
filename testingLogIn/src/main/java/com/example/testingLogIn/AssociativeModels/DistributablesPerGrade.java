package com.example.testingLogIn.AssociativeModels;

import com.example.testingLogIn.Models.Distributables;
import com.example.testingLogIn.Models.GradeLevel;
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
    @JoinColumn(name = "item")
    private Distributables item;

    @ManyToOne
    @JoinColumn(name = "gradeLevel")
    private GradeLevel gradeLevel;

    private boolean isNotDeleted = true;
}
