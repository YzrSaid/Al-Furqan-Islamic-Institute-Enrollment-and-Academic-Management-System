package com.example.testingLogIn.Models;

import com.example.testingLogIn.ModelDTO.SectionDTO;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
public class Section {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int number;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "gradeLevel")
    private GradeLevel level;
    
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "adviser")
    private UserModel adviser;
    
    private String sectionName;
    private int capacity;
    private boolean isNotDeleted;
    
    public SectionDTO toSectionDTO(){
        return SectionDTO.builder()
                                .number(getNumber())
                                .gradeLevelName(level.getLevelName())
                                .levelId(level.getLevelId())
                                .adviserName(adviser.getFullName())
                                .sectionName(getSectionName())
                                .capacity(getCapacity())
                                 .isNotDeleted(true)
                                .build();
    }

    @Override
    public String toString() {
        return level.getLevelName()+" - "+sectionName;
    }
}
