package com.example.testingLogIn.Models;

import com.example.testingLogIn.Enums.ProcessStatus;
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
public class StudentListing {
    
    @Id
    private int listNum;
    
    @ManyToOne
    @JoinColumn(name = "student")
    private Student student;
    
    @ManyToOne
    @JoinColumn(name = "syAndSem")
    private SchoolYearSemester sySem;
    
    @Enumerated(EnumType.STRING)
    private ProcessStatus status;
    
    @ManyToOne
    @JoinColumn(name = "gradeSectionToEnroll")
    private Section gradeSectionToEnroll;
}
