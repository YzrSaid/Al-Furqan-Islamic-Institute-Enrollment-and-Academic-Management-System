package com.example.testingLogIn.Models;

import com.example.testingLogIn.Enums.Semester;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
