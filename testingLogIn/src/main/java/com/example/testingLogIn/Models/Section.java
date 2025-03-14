package com.example.testingLogIn.Models;

import com.example.testingLogIn.ModelDTO.SectionDTO;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserModel;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Entity
public class Section {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int number;
    @ManyToOne
    @JoinColumn(name = "gradeLevel")
    private GradeLevel level;
    
    @ManyToOne
    @JoinColumn(name = "adviser")
    private UserModel adviser;
    
    private String sectionName;
    private int capacity;
    private boolean isNotDeleted;
    
    public SectionDTO toSectionDTO(){
        return SectionDTO.builder()
                             .number(getNumber())
                             .gradeLevelName(level.getLevelName())
                             .adviserName(adviser.getFirstname()+" "+adviser.getLastname())
                             .sectionName(getSectionName())
                             .capacity(getCapacity())
                             .isNotDeleted(true)
                             .build();
    }
    
}
