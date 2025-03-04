package com.example.testingLogIn.Models;

import com.example.testingLogIn.ModelDTO.GradeLevelToRequiredPaymentDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author magno
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class GradeLevelToRequiredPayment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int connectionId;
    
    @ManyToOne
    @JoinColumn(name = "requiredFee")
    private RequiredFees requiredFee;
    
    @ManyToOne
    @JoinColumn(name = "gradeLevel")
    private GradeLevel gradeLevel;
    
    private boolean isNotDeleted;
    
    public GradeLevelToRequiredPaymentDTO DTOmapper(){
        return GradeLevelToRequiredPaymentDTO.builder()
                            .connectionId(connectionId)
                            .requiredFeeId(requiredFee.getId())
                            .requiredFeeName(requiredFee.getName())
                            .gradeLevelId(gradeLevel.getLevelId())
                            .gradeLevelName(gradeLevel.getLevelName())
                            .requiredAmount(requiredFee.getRequiredAmount())
                            .isNotDeleted(isNotDeleted)
                            .build();
    }
}
