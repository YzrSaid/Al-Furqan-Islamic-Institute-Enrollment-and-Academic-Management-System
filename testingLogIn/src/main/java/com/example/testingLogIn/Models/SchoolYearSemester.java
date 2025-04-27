package com.example.testingLogIn.Models;

import com.example.testingLogIn.Enums.Semester;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sySemNumber;
    
    private LocalDate enrollmentDeadline;
    
    @ManyToOne
    @JoinColumn(name = "schoolYear")
    private SchoolYear schoolYear;
    
    @Enumerated(EnumType.STRING)
    private Semester sem;
    
    private boolean isFinished;
    private boolean isActive;
    private boolean isNotDeleted;

    @Override
    public String toString() {
        return schoolYear.getSchoolYear()+" - "+sem+" semester";
    }
}
