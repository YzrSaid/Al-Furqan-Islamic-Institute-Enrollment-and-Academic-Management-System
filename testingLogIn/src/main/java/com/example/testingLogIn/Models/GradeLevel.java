package com.example.testingLogIn.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
}
