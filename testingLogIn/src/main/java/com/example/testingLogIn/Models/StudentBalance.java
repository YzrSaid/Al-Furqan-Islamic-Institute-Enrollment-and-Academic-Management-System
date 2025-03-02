package com.example.testingLogIn.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 *
 * @author magno
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class StudentBalance {
    
    @Id
    @OneToOne
    @JoinColumn(name = "student", nullable = false )
    private Student student;
    
    private long currentBalance;
}
