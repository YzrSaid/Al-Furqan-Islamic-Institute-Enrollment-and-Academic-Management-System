package com.example.testingLogIn.Models;

import com.example.testingLogIn.Enums.Gender;
import com.example.testingLogIn.ModelDTO.StudentDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class Student {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int studentId;
    
    private String firstName;
    private String lastName;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private LocalDate birthdate;
    private String address;
    private String cellphoneNum;
    @OneToOne
    @JoinColumn(name = "gradeAndSection", nullable = true)
    private Section currentGradeSection;
    
    private String contactPerson;
    private String contactPersonNumber;
    private String contactPersonAddress;
    
    @JsonProperty("isNew")
    private boolean isNew;
    @JsonProperty("isTransferee")
    private boolean isTransferee;
    @JsonProperty("isScholar")
    private boolean isScholar;
    @JsonProperty("isNotDeleted")
    private boolean isNotDeleted;
    
    public StudentDTO DTOmapper(){
        return StudentDTO.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .gender(gender)
                        .birthdate(birthdate)
                        .address(address)
                        .currentSection(currentGradeSection.getLevel().getLevelName()+" - "+currentGradeSection.getSectionName())
                        .contactPersonName(contactPerson)
                        .contactPersonAddress(contactPersonAddress)
                        .contactPersonCellphone(contactPersonNumber)
                        .isNew(isNew)
                        .isTransferee(isTransferee)
                        .isNotDeleted(isNotDeleted)
                        .isScholar(isScholar)
                        .build();
    }
}
