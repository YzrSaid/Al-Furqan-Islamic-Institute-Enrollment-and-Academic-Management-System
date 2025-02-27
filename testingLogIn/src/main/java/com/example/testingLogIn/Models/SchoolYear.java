package com.example.testingLogIn.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author magno
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class SchoolYear {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int schoolYearNum;
    
    private String schoolYear;
    private boolean isActive;
    private boolean isNotDeleted;
}
