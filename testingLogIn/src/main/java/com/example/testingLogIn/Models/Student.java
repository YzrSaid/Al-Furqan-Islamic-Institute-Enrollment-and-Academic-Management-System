package com.example.testingLogIn.Models;

import com.example.testingLogIn.CustomObjects.Address;
import com.example.testingLogIn.Enums.Gender;
import com.example.testingLogIn.ModelDTO.StudentDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Optional;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(indexes = {@Index(name = "idx_full_name", columnList = "full_name")})
public class Student{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int studentId;
    
    private String studentDisplayId;
    private String firstName;
    private String lastName;
    private String middleName;

    @Column(name = "full_name", insertable = false, updatable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    private Gender gender;
    private LocalDate birthdate;
    private String street;
    private String barangay;
    private String city;
    private String cellphoneNum;
    private String birthPlace;
    private double studentBalance;
    
    @ManyToOne
    @JoinColumn(name = "gradeAndSection", nullable = true)
    private Section currentGradeSection;
    
    private String motherName;
    private String motherOccupation;
    private String fatherName;
    private String fatherOccupation;
    private String guardianName;
    private String guardianAddress;
    private String guardianContactNum;
    
    private boolean isNew;
    private boolean isScholar;
    private boolean isNotDeleted;
    
    private boolean isTransferee;
    private String madrasaName;
    private String lastGradeLevelCompleted;
    private String lastMadrasaYearCompleted;
    private String madrasaAddress;
    
    public StudentDTO DTOmapper(){
        return StudentDTO.builder()
                        .studentId(studentId)
                
                        .studentDisplayId(studentDisplayId)
                        .firstName(firstName)
                        .lastName(lastName)
                        .middleName(middleName)
                        .fullName(fullName)
                        .gender(gender)
                        .birthdate(birthdate)
                        .birthPlace(birthPlace)
                        .address(Address.builder().street(street).barangay(barangay).city(city).build())
                        .balanceAmount(studentBalance)
                        .currentGradeLevel(Optional.ofNullable(currentGradeSection).map(Section::getLevel).orElse(null))
                        .currentGradeSection(Optional.ofNullable(currentGradeSection).map(sec -> sec.getLevel().getLevelName()).orElse("")
                                            +Optional.ofNullable(currentGradeSection).map(sec ->"-"+ sec.getSectionName()).orElse("NONE"))
                
                        .motherName(motherName)
                        .motherOccupation(motherOccupation)
                        .fatherName(fatherName)
                        .fatherOccupation(fatherOccupation)
                        .guardianName(guardianName)
                        .guardianAddress(guardianAddress)
                        .guardianContactNum(guardianContactNum)
                        .isNew(isNew)
                        .isNotDeleted(isNotDeleted)
                        .isScholar(isScholar)
                
                        .isTransferee(isTransferee)
                        .madrasaName(madrasaName)
                        .lastGradeLevelCompleted(lastGradeLevelCompleted)
                        .lastMadrasaYearCompleted(lastMadrasaYearCompleted)
                        .madrasaAddress(madrasaAddress)
                        .build();
    }
}
