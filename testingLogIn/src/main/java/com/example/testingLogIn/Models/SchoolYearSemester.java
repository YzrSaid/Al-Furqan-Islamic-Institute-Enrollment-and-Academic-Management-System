package com.example.testingLogIn.Models;

import com.example.testingLogIn.Enums.Semester;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

/**
 *
 * @author magno
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class SchoolYearSemester {
    
    @Id
    private int sySemNumber;
    
    @ManyToOne
    @JoinColumn(name = "schoolYear")
    private SchoolYear schoolYear;
    
    @Enumerated(EnumType.STRING)
    private Semester sem;
    
    private boolean isActive;
    private boolean isNotDeleted;
}
